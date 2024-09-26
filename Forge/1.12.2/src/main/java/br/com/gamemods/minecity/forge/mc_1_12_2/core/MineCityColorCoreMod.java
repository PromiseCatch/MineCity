package br.com.gamemods.minecity.forge.mc_1_12_2.core;

import br.com.gamemods.minecity.forge.base.core.ModEnv;
import br.com.gamemods.minecity.forge.base.core.deploader.DepLoader;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.util.Map;

@IFMLLoadingPlugin.Name("MineCityCore")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({
        "br.com.gamemods.minecity.forge.mc_1_12_2.core",
        "br.com.gamemods.minecity.forge.base.core",
})
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class MineCityColorCoreMod implements IFMLLoadingPlugin, IFMLCallHook {
    @Override
    public Void call() throws Exception {
        File mcDir = (File) FMLInjectionData.data()[6];
        new DepLoader(
                new File(mcDir, "MineCity/libs"),
                (LaunchClassLoader) MineCityColorCoreMod.class.getClassLoader(),
                FMLInjectionData.data(),
                Loader.class,
                ComparableVersion::new
        ).load();
        return null;
    }
    
    @Override
    public String[] getASMTransformerClass() {
        ModEnv.hookClass = "br.com.gamemods.minecity.forge.mc_1_12_2.protection.MineCityColorHooks";
        ModEnv.rayTraceResultClass = "net.minecraft.util.math.RayTraceResult";
        ModEnv.aabbClass = "net.minecraft.util.math.AxisAlignedBB";

        return new String[]{
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.PartFormationPlaneTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.PartAnnihilationPaneTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.IPartHostTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.AEBasePartTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.PartPlacementTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.ToolMassCannonTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.WirelessTerminalGuiObjectTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.BlockTinyTNTTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.appeng.EntityTinyTNTPrimedTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityTeleporterTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityTerraTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityMinerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityCropmatronTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityTeslaTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityRecyclerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.ExplosionIC2Transformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.EntityIC2ExplosiveTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.EntityDynamiteTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.ICropTileTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.CropCardTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.TileEntityCropTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.industrialcraft.EntityParticleTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.InventoryTransferDClassTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.TransposerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.AdapterTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.InventoryWorldControlMk2DClassTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.MagnetProviderTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.UpgradePistonTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.UpgradeTractorBeamTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.UpgradeLeashTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.TankWorldControlDClassTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.TileRobotProxyTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.InventoryWorldControlDClassTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.TextBufferTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.opencomputers.PacketHandlerDTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.pamharvestcraft.BlockPamSaplingTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.mrcrayfishfurniture.MessageTVServerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.wrcbe.EntityREPTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.wrcbe.JammerPartTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.wrcbe.WirelessBoltTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.zettaindustries.QuarryFixerBlockTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.zettaindustries.BlockSulfurTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveintegration.TileItemRobinTransformer",
                // TODO: Check if pump still existing on 1.12
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.TileEntityFluidPumpTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.TileEntityConveyorSorterTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.BlockMetalDevicesTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.ItemIEToolTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.ChemthrowerEffectTeleportTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.ChemthrowerHandlerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.immersiveengineering.EntityChemthrowerShotTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.forgemultipart.ButtonPartTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.mod.forgemultipart.BlockMultiPartTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.forgemultipart.EventHandlerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.mod.ModInterfacesTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityPlayerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityLivingBaseTransformer",
                "br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorExplosionTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.item.ItemTransformer",
                // TODO: Fix
                // "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockPistonBaseTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.item.ItemBucketTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockChorusFlowerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.NodeProcessorTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.PathFinderTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.world.ChunkCacheTransformer",
                // TODO: Compatibility with Sponge
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.OnImpactTransformer",
                // TODO: Fix
                // "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityEggTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockStemTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockSaplingTransformer",
                // TODO: Fix Pure Forge issue
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.block.GrowMonitorTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockDragonEggTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityXPOrbTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityArrowTransformer",
                // TODO: Fix Pure Forge issue
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityIgnitionTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityEnderCrystalTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockTNTTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityArmorStandTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityFishingHookTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityAreaEffectCloudTransformer",
                "br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorInterfaceTransformer",
                // TODO: Compatibility with Sponge
                //"br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorEntityPotionTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityBoatTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.EntityMinecartTransformer",
                // TODO: Fix
                //"br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorWorldServerTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.world.ChunkTransformer",
                "br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorEntityPlayerMPTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockOpenReactorTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockClickReactorTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockClickExtendsOpenTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockModifyExtendsOpenTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.block.BlockNoReactExtendsOpenTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.item.ItemModifyFaceReactorTransformer",
                "br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.ProjectileTransformer",
                // TODO: Fix Pure Forge issue
                //"br.com.gamemods.minecity.forge.base.core.transformer.forge.entity.AddPotionEffectObserverTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return getClass().getName();
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // Nothing to be injected here
    }

    @Override
    public String getAccessTransformerClass() {
        return "br.com.gamemods.minecity.forge.base.core.transformer.MineCityAT";
    }
}
