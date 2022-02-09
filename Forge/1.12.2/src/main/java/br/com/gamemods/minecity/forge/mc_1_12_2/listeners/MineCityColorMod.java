package br.com.gamemods.minecity.forge.mc_1_12_2.listeners;

import br.com.gamemods.minecity.api.MathUtil;
import br.com.gamemods.minecity.api.Slow;
import br.com.gamemods.minecity.api.command.LegacyFormat;
import br.com.gamemods.minecity.datasource.api.DataSourceException;
import br.com.gamemods.minecity.forge.base.MineCityForge;
import br.com.gamemods.minecity.forge.base.command.RootCommand;
import br.com.gamemods.minecity.forge.base.core.ModEnv;
import br.com.gamemods.minecity.forge.mc_1_12_2.MineCityColor;
import br.com.gamemods.minecity.forge.mc_1_12_2.command.ColorTransformer;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.ColorIndustrialCraftListener;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.ColorSnapshotHandler;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.opencomputers.ColorRobotProtections;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.vanilla.ColorBlockProtections;
import br.com.gamemods.minecity.forge.mc_1_12_2.protection.vanilla.ColorEntityProtections;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.relauncher.Side;
import org.xml.sax.SAXException;

import java.io.IOException;

@Mod(modid = ModEnv.MOD_ID, name = ModEnv.MOD_ID, version = ModEnv.MOD_VERSION, acceptableRemoteVersions = "*",
        acceptedMinecraftVersions = "[1.12.2]", dependencies = "before:opencomputers;before:CoFHCore"
)
public class MineCityColorMod {
    private MineCityColor forge;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) throws IOException, SAXException {
        MathUtil.sin = MathHelper::sin;
        MathUtil.cos = MathHelper::cos;

        forge = new MineCityColor();
        forge.logger = event.getModLog();
        forge.selectionTool = Items.WOODEN_HOE;
        forge.selectionPallet = selection -> {
          selection.cornerA = Blocks.GLOWSTONE.getDefaultState();
          selection.cornerB = Blocks.LIT_REDSTONE_LAMP.getDefaultState();
          selection.corners = Blocks.SEA_LANTERN.getDefaultState();
          selection.linesA = Blocks.GOLD_BLOCK.getDefaultState();
          selection.linesB = Blocks.LAPIS_BLOCK.getDefaultState();
          selection.lines = Blocks.PRISMARINE.getDefaultState();
          selection.extension = Blocks.GLOWSTONE.getDefaultState();
        };

        LegacyFormat.BLACK.server = TextFormatting.BLACK;
        LegacyFormat.DARK_BLUE.server = TextFormatting.DARK_BLUE;
        LegacyFormat.DARK_GREEN.server = TextFormatting.DARK_GREEN;
        LegacyFormat.DARK_AQUA.server = TextFormatting.DARK_AQUA;
        LegacyFormat.DARK_RED.server = TextFormatting.DARK_RED;
        LegacyFormat.DARK_PURPLE.server = TextFormatting.DARK_PURPLE;
        LegacyFormat.GOLD.server = TextFormatting.GOLD;
        LegacyFormat.GRAY.server = TextFormatting.GRAY;
        LegacyFormat.DARK_GRAY.server = TextFormatting.DARK_GRAY;
        LegacyFormat.BLUE.server = TextFormatting.BLUE;
        LegacyFormat.GREEN.server = TextFormatting.GREEN;
        LegacyFormat.AQUA.server = TextFormatting.AQUA;
        LegacyFormat.RED.server = TextFormatting.RED;
        LegacyFormat.LIGHT_PURPLE.server = TextFormatting.LIGHT_PURPLE;
        LegacyFormat.YELLOW.server = TextFormatting.YELLOW;
        LegacyFormat.WHITE.server = TextFormatting.WHITE;
        LegacyFormat.RESET.server = TextFormatting.RESET;
        LegacyFormat.MAGIC.server = TextFormatting.OBFUSCATED;
        LegacyFormat.BOLD.server = TextFormatting.BOLD;
        LegacyFormat.STRIKE.server = TextFormatting.STRIKETHROUGH;
        LegacyFormat.UNDERLINE.server = TextFormatting.UNDERLINE;
        LegacyFormat.ITALIC.server = TextFormatting.ITALIC;

        forge.onPreInit(new Configuration(event.getSuggestedConfigurationFile()), event.getModLog(), new ColorTransformer());
        MineCityForge.snapshotHandler = new ColorSnapshotHandler();
        MinecraftForge.EVENT_BUS.register(new ColorTickListener(forge));
        MinecraftForge.EVENT_BUS.register(new ColorToolListener(forge));
        MinecraftForge.EVENT_BUS.register(new ColorWorldListener(forge));
        MinecraftForge.EVENT_BUS.register(ModEnv.blockProtections = new ColorBlockProtections(forge));
        MinecraftForge.EVENT_BUS.register(ModEnv.entityProtections = new ColorEntityProtections(forge));
    }

    @Mod.EventHandler
    @Optional.Method(modid = "IC2")
    public void onPostInitIC(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ColorIndustrialCraftListener(forge));
    }

    @Mod.EventHandler
    @Optional.Method(modid = "OpenComputers")
    public void onPostInitOC(FMLPostInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new ColorRobotProtections(forge));
    }

    @Slow
    @Mod.EventHandler
    public void onServerStart(FMLServerAboutToStartEvent event) throws IOException, DataSourceException, SAXException {
        forge.onServerAboutToStart(event.getServer(), event.getSide() == Side.CLIENT);
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        forge.mineCity.commands.getRootCommands().stream()
                .map(name->forge.mineCity.commands.get(name).get())
                .map(r->r.command).distinct()
                .forEach(i-> event.registerServerCommand(new RootCommand<>(forge, i)));
    }

    @Slow
    @Mod.EventHandler
    public void onServerStop(FMLServerStoppedEvent event) throws DataSourceException {
        forge.onServerStop();
    }
}
