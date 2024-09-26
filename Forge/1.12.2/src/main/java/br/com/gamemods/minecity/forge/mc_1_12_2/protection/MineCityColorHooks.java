package br.com.gamemods.minecity.forge.mc_1_12_2.protection;

import br.com.gamemods.minecity.api.shape.Point;
import br.com.gamemods.minecity.forge.base.MineCityForge;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityLivingBase;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityPlayerMP;
import br.com.gamemods.minecity.forge.base.accessors.entity.projectile.OnImpact;
import br.com.gamemods.minecity.forge.base.accessors.item.IItem;
import br.com.gamemods.minecity.forge.base.accessors.item.IItemStack;
import br.com.gamemods.minecity.forge.base.accessors.world.IWorldServer;
import br.com.gamemods.minecity.forge.base.core.ModEnv;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockDragonEggTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockPistonBaseTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockTNTTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.block.GrowMonitorTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.*;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.IPartHostTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.BiomeUtilTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.EntityParticleTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityTeleporterTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityTerraTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.AdapterTransformer;
import br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.UpgradeTractorBeamTransformer;
import br.com.gamemods.minecity.forge.mc_1_12_2.ColorUtil;
import br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorEntityPotionTransformer;
import br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorWorldServerTransformer;
import br.com.gamemods.minecity.forge.mc_1_12_2.event.*;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

@Referenced
public class MineCityColorHooks {
    public static volatile Entity spawner;
    public static Object pistonMovedBy;

    @Referenced(at = UpgradeTractorBeamTransformer.class)
    public static void setPistonMovedBy(Object cause) {
        pistonMovedBy = cause;
    }

    @Referenced(at = BlockPistonBaseTransformer.class)
    public static boolean onPistonMove(boolean ret, Throwable ex, Object blockObj, World world, BlockPos pos, EnumFacing dir, boolean extend) throws Throwable {
        world.captureBlockSnapshots = false;
        List<BlockSnapshot> changes = new ArrayList<>(world.capturedBlockSnapshots);
        world.capturedBlockSnapshots.clear();
        Object movedBy = pistonMovedBy;
        pistonMovedBy = null;

        try {
            IBlockState state = changes.stream()
                    .filter(snap -> snap.getPos().equals(pos)).map(BlockSnapshot::getReplacedBlock)
                    .findFirst().orElseGet(() -> world.getBlockState(pos));

            if (MinecraftForge.EVENT_BUS.post(new PistonMoveEvent(world, pos, state, dir, extend, changes, movedBy))) {
                revertChanges(changes);
                ret = false;
            } else sendUpdates(changes);
        } catch(Exception e) {
            revertChanges(changes);
            throw e;
        }
        if(ex != null) throw ex;
        return ret;
    }
    @Referenced(at = PathFinderTransformer.class)
    public static boolean onPathFind(PathFinder pathFinder, PathPoint point, IBlockAccess access, EntityLiving entity) {
        return ModEnv.entityProtections.onPathFind(pathFinder, point, access, entity);
    }

    public static void onImpact(Entity entity, RayTraceResult result) {
        if(MinecraftForge.EVENT_BUS.post(new PreImpactEvent(entity, result))) {
            entity.setDead();
            return;
        }

        World worldObj = entity.world;
        try {
            spawner = entity;
            worldObj.captureBlockSnapshots = true;

            ((OnImpact) entity).mineCityOnImpact(result);

            worldObj.captureBlockSnapshots = false;
            spawner = null;

            ArrayList<BlockSnapshot> changes = new ArrayList<>(worldObj.capturedBlockSnapshots);
            worldObj.capturedBlockSnapshots.clear();

            if(MinecraftForge.EVENT_BUS.post(new PostImpactEvent(entity, result, changes)))
                revertChanges(changes);
            else
                sendUpdates(changes);
        } catch(Exception e) {
            revertChanges(new ArrayList<>(worldObj.capturedBlockSnapshots));
            throw e;
        } finally {
            spawner = null;
            worldObj.captureBlockSnapshots = false;
            worldObj.capturedBlockSnapshots.clear();
        }
    }

    @Referenced(at = OnImpactTransformer.class)
    public static void onFireBallImpact(EntityFireball fireball, RayTraceResult result)
    {
        onImpact(fireball, result);
    }

    @Referenced(at = OnImpactTransformer.class)
    public static void onThrowableImpact(EntityThrowable throwable, RayTraceResult result) {
        onImpact(throwable, result);
    }

    @Referenced(at = EntityEggTransformer.class)
    public static boolean onEggSpawnChicken(EntityEgg egg) {
        return MinecraftForge.EVENT_BUS.post(new EggSpawnChickenEvent(egg));
    }

    @SuppressWarnings("unchecked")
    private static void revertChanges(List<BlockSnapshot> changes) {
        MineCityForge.snapshotHandler.restore((List) changes);
    }

    @SuppressWarnings("unchecked")
    private static void sendUpdates(List<BlockSnapshot> changes) {
        MineCityForge.snapshotHandler.send((List) changes);
    }

    @Contract("!null, _, _, _, _ -> fail")
    @Referenced(at = GrowMonitorTransformer.class)
    public static void onGrowableGrow(Throwable thrown, Object source, World world, BlockPos pos, IBlockState state) throws Throwable {
        world.captureBlockSnapshots = false;
        if (world.capturedBlockSnapshots.isEmpty()) {
            if(thrown != null) throw thrown;
            return;
        }

        ArrayList<BlockSnapshot> changes = new ArrayList<>(world.capturedBlockSnapshots);
        world.capturedBlockSnapshots.clear();

        if(MinecraftForge.EVENT_BUS.post(new BlockGrowEvent(world, pos, state, source, changes))) revertChanges(changes);
        else sendUpdates(changes);

        if(thrown != null) throw thrown;
    }

    @Referenced(at = BlockDragonEggTransformer.class)
    public static void startCapturingBlocks(World world) {
        world.captureBlockSnapshots = true;
    }

    @Referenced(at = BlockDragonEggTransformer.class)
    public static void onDragonEggTeleport(BlockDragonEgg block, EntityPlayer player, World world, BlockPos pos, IBlockState state) {
        world.captureBlockSnapshots = false;
        ArrayList<BlockSnapshot> changes = new ArrayList<>(world.capturedBlockSnapshots);
        world.capturedBlockSnapshots.clear();

        if (MinecraftForge.EVENT_BUS.post(new PlayerTeleportDragonEggEvent(player, world, pos, state, changes))) {
            changes.forEach(snapshot -> {
                world.restoringBlockSnapshots = true;
                snapshot.restore(true, false);
                world.restoringBlockSnapshots = false;
            });
        }
    }

    @Referenced(at = EntityFishingHookTransformer.class)
    public static Entity onFishingHookSpawnEntity(Entity entity, EntityFishHook hook) {
        MinecraftForge.EVENT_BUS.post(new EntitySpawnByFishingHookEvent(entity, hook));
        return entity;
    }

    @Referenced(at = EntityXPOrbTransformer.class)
    public static EntityPlayer onXpOrbTargetPlayer(EntityPlayer player, EntityXPOrb orb) {
        if(player == null) return null;

        Event event = new XpOrbTargetPlayerEvent(player, orb);
        if(MinecraftForge.EVENT_BUS.post(event)) return null;
        else return player;
    }

    @Referenced(at = EntityArrowTransformer.class)
    public static boolean onPlayerPickupArrow(EntityArrow arrow, EntityPlayer player) {
        Event event = new PlayerPickupArrowEvent(player, arrow);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Referenced(at = EntityIgnitionTransformer.class)
    public static boolean onIgnite(Entity entity, int fireTicks, @Nullable Object source, Class<?> sourceClass, String method, String desc, Object[] methodParams) {
        return MinecraftForge.EVENT_BUS.post(new EntityIgniteEvent(
                entity, fireTicks, source, sourceClass, method, desc, methodParams
        ));
    }

    @Referenced(at = AddPotionEffectObserverTransformer.class)
    public static boolean onEntityReceivePotionEffect
            (EntityLivingBase mcEntity, PotionEffect mcEffect, Object source, Class<?> sourceClass,
             String methodName, String methodDesc, Object[] methodParams) {
        return MinecraftForge.EVENT_BUS.post(new EntityReceivePotionEffect(
                mcEntity, mcEffect, source, sourceClass, methodName, methodDesc, methodParams
        ));
    }

    @Referenced(at = EntityEnderCrystalTransformer.class)
    public static boolean onEntityDamage(Entity entity, DamageSource source, float amount) {
        EntityDamageEvent event = new EntityDamageEvent(entity, source, amount);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Referenced(at = BlockTNTTransformer.class)
    public static boolean onArrowIgnite(World world, BlockPos pos, IBlockState state, EntityArrow arrow) {
        ProjectileModifyBlockEvent event = new ProjectileModifyBlockEvent(arrow, world, pos, state);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Referenced(at = EntityBoatTransformer.class)
    @Referenced(at = EntityMinecartTransformer.class)
    public static boolean onVehicleDamage(Entity entity, DamageSource source, float amount) {
        VehicleDamageEvent event = new VehicleDamageEvent(entity, source, amount);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Referenced(at = ColorEntityPotionTransformer.class)
    public static void onPotionApplyEffect(EntityLivingBase entity, PotionEffect effect, Entity potion) {
        PotionApplyEvent event = new PotionApplyEvent(entity, effect, potion);
        if(!MinecraftForge.EVENT_BUS.post(event)) entity.addPotionEffect(effect);
    }

    @Contract("null, _ -> null")
    @Referenced(at = EntityFishingHookTransformer.class)
    public static Entity onFishingHookHitEntity(Entity entity, EntityFishHook hook) {
        if(entity == null) return null;

        FishingHookHitEntityEvent event = new FishingHookHitEntityEvent(entity, hook);
        if(MinecraftForge.EVENT_BUS.post(event)) return null;
        else return entity;
    }

    @Referenced(at = EntityFishingHookTransformer.class)
    public static boolean onFishingHookBringEntity(EntityFishHook hook) {
        FishingHookBringEntityEvent event = new FishingHookBringEntityEvent(hook.caughtEntity, hook);
        return MinecraftForge.EVENT_BUS.post(event);
    }

    @Referenced(at = AdapterTransformer.class)
    @Referenced(at = EntityParticleTransformer.class)
    @Referenced(at = TileEntityTerraTransformer.class)
    @Referenced(at = BiomeUtilTransformer.class)
    @Referenced(at = TileEntityTeleporterTransformer.class)
    public static Point toPoint(Object obj) {
        Vec3i pos = (Vec3i) obj;
        return new Point(pos.getX(), pos.getY(), pos.getZ());
    }

    @SuppressWarnings("unchecked")
    @Referenced(at = IPartHostTransformer.class)
    public static br.com.gamemods.minecity.api.world.BlockPos toPos(Object obj, int x, int y, int z) {
        World world;
        if (obj instanceof World) world = (World) obj;
        else if (obj instanceof Option) world = (World) ((Option)obj).get();
        else throw new UnsupportedOperationException(obj.getClass().toString());

        return new br.com.gamemods.minecity.api.world.BlockPos(ModEnv.blockProtections.mod.world(world), x, y, z);
    }

    @Referenced(at = EntityLivingBaseTransformer.class)
    public static boolean onLivingSwing(IItem item, IEntityLivingBase living, IItemStack stack) {
        return !((EntityLivingBase) living).world.isRemote &&
                ModEnv.entityProtections.onLivingSwing(item, living, stack);
    }

    @Referenced(at = ColorWorldServerTransformer.class)
    public static boolean canMineBlock(World mcWorld, EntityPlayer mcPlayer, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        IWorldServer world = (IWorldServer) mcWorld;
        return ModEnv.blockProtections.onBlockBreak(mcPlayer,
                world.getIState(x, y, z),
                new br.com.gamemods.minecity.api.world.BlockPos(ModEnv.blockProtections.mod.world(world), x, y, z),
                false
        );
    }

    @Referenced(at = EntityPlayerTransformer.class)
    public static boolean canPlayerEdit(EntityPlayer player, BlockPos mcPos, EnumFacing facing, ItemStack stack) {
        return !player.world.isRemote && ModEnv.blockProtections.onPlayerCheckEdit(
                (IEntityPlayerMP) player,
                mcPos.getX(), mcPos.getY(), mcPos.getZ(),
                ColorUtil.toDirection(facing),
                (IItemStack) (Object) stack
        );
    }

    private MineCityColorHooks(){}
}
