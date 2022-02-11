package br.com.gamemods.minecity.sponge.data.value;

import br.com.gamemods.minecity.reactive.game.block.data.TileEntityData;
import br.com.gamemods.minecity.sponge.data.manipulator.reactive.SpongeManipulator;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;

public class SpongeTileEntityData implements TileEntityData, DataSerializable
{
    private final SpongeManipulator manipulator;
    private final TileEntity tileEntity;

    public SpongeTileEntityData(SpongeManipulator manipulator, TileEntity tileEntity)
    {
        this.manipulator = manipulator;
        this.tileEntity = tileEntity;
    }

    @Override
    public TileEntity getTileEntity()
    {
        return tileEntity;
    }

    @Override
    public String toString()
    {
        return "SpongeTileEntityData{"+
                "tileEntity="+tileEntity+
                '}';
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return DataContainer.createNew();
    }
}
