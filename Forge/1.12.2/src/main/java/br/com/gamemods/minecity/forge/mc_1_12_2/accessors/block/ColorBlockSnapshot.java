package br.com.gamemods.minecity.forge.mc_1_12_2.accessors.block;

import br.com.gamemods.minecity.api.world.BlockPos;
import br.com.gamemods.minecity.forge.base.MineCityForge;
import br.com.gamemods.minecity.forge.base.accessors.block.IBlockSnapshot;
import br.com.gamemods.minecity.forge.base.accessors.block.IState;
import br.com.gamemods.minecity.forge.base.accessors.world.IWorldServer;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorInterfaceTransformer;
import net.minecraftforge.common.util.BlockSnapshot;

@Referenced(at = ColorInterfaceTransformer.class)
public interface ColorBlockSnapshot extends IBlockSnapshot {
    @Override
    default BlockPos getPosition(MineCityForge mod) {
        net.minecraft.util.math.BlockPos pos = ((BlockSnapshot) this).getPos();
        return new BlockPos(mod.world(getIWorld()), pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    default int getX() {
        return ((BlockSnapshot) this).getPos().getX();
    }

    @Override
    default int getY() {
        return ((BlockSnapshot) this).getPos().getY();
    }

    @Override
    default int getZ() {
        return ((BlockSnapshot) this).getPos().getZ();
    }

    @Override
    default IWorldServer getIWorld() {
        return (IWorldServer) ((BlockSnapshot) this).getWorld();
    }

    @Override
    default IState getCurrentState() {
        return (IState) ((BlockSnapshot) this).getCurrentBlock();
    }

    @Override
    default IState getReplacedState() {
        return (IState) ((BlockSnapshot) this).getReplacedBlock();
    }
}
