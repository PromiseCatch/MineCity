package br.com.gamemods.minecity.forge.mc_1_12_2.protection.vanilla;

import br.com.gamemods.minecity.api.world.BlockPos;
import br.com.gamemods.minecity.forge.base.MineCityForge;
import br.com.gamemods.minecity.forge.base.accessors.IRayTraceResult;
import br.com.gamemods.minecity.forge.base.accessors.block.IState;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntity;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityLivingBase;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityPlayerMP;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IPotionEffect;
import br.com.gamemods.minecity.forge.base.accessors.entity.item.IEntityItem;
import br.com.gamemods.minecity.forge.base.accessors.entity.item.IEntityXPOrb;
import br.com.gamemods.minecity.forge.base.accessors.entity.projectile.EntityProjectile;
import br.com.gamemods.minecity.forge.base.accessors.entity.projectile.IEntityArrow;
import br.com.gamemods.minecity.forge.base.accessors.entity.projectile.IEntityFishHook;
import br.com.gamemods.minecity.forge.base.accessors.item.IItemStack;
import br.com.gamemods.minecity.forge.base.accessors.world.IExplosion;
import br.com.gamemods.minecity.forge.base.accessors.world.IWorldServer;
import br.com.gamemods.minecity.forge.base.command.ForgePlayer;
import br.com.gamemods.minecity.forge.base.protection.vanilla.EntityProtections;
import br.com.gamemods.minecity.forge.mc_1_12_2.event.*;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.MineCityColorHooks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.AbstractList;
import java.util.List;

public class ColorEntityProtections extends EntityProtections {
    public ColorEntityProtections(MineCityForge mod) {
        super(mod);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityReceivePotionEffect(EntityReceivePotionEffect event) {
        if (event.getEntity().world.isRemote) return;

        if (onEntityReceivePotionEffect(
                (IEntityLivingBase) event.getEntityLiving(),
                (IPotionEffect) event.effect,
                event.source, event.sourceClass, event.methodName, event.methodDesc, event.methodParams
        ))
        {
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onExplosion(ExplosionEvent.Detonate event) {
        if (event.getWorld().isRemote) return;

        onExplosionDetonate(
                (IEntity) MineCityColorHooks.spawner,
                (IWorldServer) event.getWorld(),
                (IExplosion) event.getExplosion(),
                (List) event.getAffectedEntities(),
                new AbstractList<BlockPos>() {
                    List<net.minecraft.util.math.BlockPos> base = event.getAffectedBlocks();
                    IWorldServer world = (IWorldServer) event.getWorld();
                    br.com.gamemods.minecity.api.world.BlockPos last;

                    @Override
                    public br.com.gamemods.minecity.api.world.BlockPos get(int index) {
                        net.minecraft.util.math.BlockPos cp = base.get(index);
                        br.com.gamemods.minecity.api.world.BlockPos bp;
                        if(last == null)
                            last = bp = new br.com.gamemods.minecity.api.world.BlockPos(mod.world(world), cp.getX(), cp.getY(), cp.getZ());
                        else
                            last = bp = new br.com.gamemods.minecity.api.world.BlockPos(last, cp.getX(), cp.getY(), cp.getZ());

                        bp.getChunk();
                        return bp;
                    }

                    @Override
                    public br.com.gamemods.minecity.api.world.BlockPos set(int index, br.com.gamemods.minecity.api.world.BlockPos pos) {
                        br.com.gamemods.minecity.api.world.BlockPos prev = get(index);
                        base.set(index, new net.minecraft.util.math.BlockPos(pos.x, pos.y, pos.z));
                        return prev;
                    }

                    @Override
                    public br.com.gamemods.minecity.api.world.BlockPos remove(int index) {
                        br.com.gamemods.minecity.api.world.BlockPos removed = get(index);
                        base.remove(index);
                        return removed;
                    }

                    @Override
                    public boolean remove(Object o) {
                        if (o instanceof br.com.gamemods.minecity.api.world.BlockPos) {
                            br.com.gamemods.minecity.api.world.BlockPos pos = (br.com.gamemods.minecity.api.world.BlockPos) o;
                            return base.remove(new net.minecraft.util.math.BlockPos(pos.x, pos.y, pos.z));
                        }
                        return false;
                    }

                    @Override
                    public void add(int index, br.com.gamemods.minecity.api.world.BlockPos pos) {
                        base.add(index, new net.minecraft.util.math.BlockPos(pos.x, pos.y, pos.z));
                    }

                    @Override
                    public boolean add(br.com.gamemods.minecity.api.world.BlockPos pos) {
                        return base.add(new net.minecraft.util.math.BlockPos(pos.x, pos.y, pos.z));
                    }

                    @Override
                    public int size() {
                        return base.size();
                    }
                }
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPreImpact(PreImpactEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onPreImpact(
                (IEntity) event.getEntity(),
                (IRayTraceResult) event.traceResult
        )) {
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPostImpact(PostImpactEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onPostImpact(
                (IEntity) event.getEntity(),
                (IRayTraceResult) event.traceResult,
                (List) event.changes
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityEnterWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isRemote) return;

        Entity entity = event.getEntity();
        if (onEntityEnterWorld(
                (IEntity) entity,
                new br.com.gamemods.minecity.api.world.BlockPos(mod.world(event.getWorld()), (int) entity.posX, (int) entity.posY, (int) entity.posZ),
                (IEntity) MineCityColorHooks.spawner
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEggSpawnChicken(EggSpawnChickenEvent event) {
        if (event.getEntity().world.isRemote) return;

        if(onEggSpawnChicken((EntityProjectile) event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntitySpawnByFishingHook(EntitySpawnByFishingHookEvent event) {
        if (event.getEntity().world.isRemote) return;

        onEntitySpawnByFishingHook(
                (IEntity) event.getEntity(),
                (IEntityFishHook) event.hook
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDropsXp(LivingExperienceDropEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        onLivingDropsExp(
                (IEntityLivingBase) event.getEntityLiving(),
                (IEntityPlayerMP) event.getAttackingPlayer(),
                event.getDroppedExperience()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingDrops(LivingDropsEvent event) {
        if (event.getEntityLiving().world.isRemote) return;

        onLivingDrops(
                (IEntityLivingBase) event.getEntityLiving(),
                event.getSource(),
                event.getDrops()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDrops(PlayerDropsEvent event) {
        if (event.getEntityPlayer().world.isRemote) return;

        onPlayerDrops(
                (IEntityPlayerMP) event.getEntityPlayer(),
                event.getSource(),
                event.getDrops()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onItemToss(ItemTossEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onItemToss(
                (IEntityPlayerMP) event.getPlayer(),
                (IEntityItem) event.getEntityItem()
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onXpOrbTargetPlayerEvent(XpOrbTargetPlayerEvent event) {
        if (event.getEntityPlayer().world.isRemote) return;

        if (onXpOrbTargetPlayerEvent(
                (IEntityPlayerMP) event.getEntityPlayer(),
                (IEntityXPOrb) event.orb
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerPickupExpEvent(PlayerPickupXpEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onPlayerPickupExpEvent(
                (IEntityPlayerMP) event.getEntityPlayer(),
                (IEntityXPOrb) event.getOrb()
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerPickupArrowEvent(PlayerPickupArrowEvent event) {
        if (event.arrow.world.isRemote) return;

        if (onPlayerPickupArrowEvent(
                (IEntityPlayerMP) event.getEntityPlayer(),
                (IEntityArrow) event.arrow
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onProjectileModifyBlock(ProjectileModifyBlockEvent event) {
        if (event.getWorld().isRemote) return;

        net.minecraft.util.math.BlockPos pos = event.getPos();
        if (onProjectileModifyBlock(
                (IEntity) event.projectile,
                (IState) event.getState(),
                (IWorldServer) event.getWorld(),
                pos.getX(), pos.getY(), pos.getZ()
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote) return;

        if (onPlayerInteractEntity(
                (IEntityPlayerMP) event.getEntityPlayer(),
                (IEntity) event.getTarget(),
                (IItemStack) (Object) event.getItemStack(),
                event.getHand() == EnumHand.OFF_HAND
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityConstruct(EntityEvent.EntityConstructing event) {
        if (event.getEntity().world.isRemote) return;

        mod.callSpawnListeners((IEntity) event.getEntity());
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityEnterChunk(EntityEvent.EnteringChunk event) {
        if (event.getEntity().world.isRemote) return;

        onEntityEnterChunk(
                event.getEntity(),
                event.getOldChunkX(),
                event.getOldChunkZ(),
                event.getNewChunkX(),
                event.getNewChunkZ()
        );
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFishingHookHitEntity(FishingHookHitEntityEvent event) {
        if (event.hook.world.isRemote) return;

        if (onFishingHookHitEntity(
                (IEntity) event.getEntity(),
                (EntityProjectile) event.hook
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onFishingHookBringEntity(FishingHookBringEntityEvent event) {
        if (event.hook.world.isRemote) return;

        if (onFishingHookBringEntity(
                (IEntity) event.getEntity(),
                (EntityProjectile) event.hook
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPotionApply(PotionApplyEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onPotionApply(
                (IEntityLivingBase) event.getEntityLiving(),
                (IPotionEffect) event.effect,
                (IEntity) event.potion
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerPickupItem(EntityItemPickupEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onPlayerPickupItem(
                (IEntityPlayerMP) event.getEntityPlayer(),
                (IEntityItem) event.getItem(),
                false
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityIgniteEntityEvent(EntityIgniteEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onEntityIgniteEvent(
                (IEntity) event.getEntity(),
                event.ticks,
                event.source, event.sourceClass,
                event.sourceMethod, event.sourceMethodDesc,
                event.methodParams
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerAttack(AttackEntityEvent event) {
        if (event.getEntity().world.isRemote) return;

        EntityPlayer entity = event.getEntityPlayer();
        ForgePlayer player = mod.player(entity);
        if (onPlayerAttack(
                (IEntityPlayerMP) entity,
                (IEntity) event.getTarget(),
                (IItemStack) (Object) (player.offHand? entity.getHeldItemOffhand() : entity.getHeldItemMainhand()),
                player.offHand
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onLivingAttack(LivingAttackEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onEntityDamage(
                (IEntityLivingBase) event.getEntityLiving(),
                event.getSource(),
                event.getAmount(),
                false
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onVehicleDamage(VehicleDamageEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onEntityDamage(
                (IEntity) event.getEntity(),
                event.source,
                event.amount,
                false
        )) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().world.isRemote) return;

        if (onEntityDamage(
                (IEntity) event.getEntity(),
                event.source,
                event.amount,
                false
        )) {
            event.setCanceled(true);
        }
    }
}
