package br.com.gamemods.minecity.sponge.data.manipulator.boxed;

import br.com.gamemods.minecity.reactive.game.block.data.TileEntityData;
import br.com.gamemods.minecity.sponge.data.value.SpongeTileEntityData;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

public class TileEntityDataManipulator extends SingleData<TileEntityData, TileEntityDataManipulator, TileEntityDataManipulator.Immutable>
{
    public TileEntityDataManipulator(TileEntityData value)
    {
        super(value, MineCityKeys.TILE_ENTITY_DATA);
    }

    @Override
    public TileEntityDataManipulator copy()
    {
        return new TileEntityDataManipulator(getValue());
    }

    @Override
    public Immutable asImmutable()
    {
        return new Immutable(getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(MineCityKeys.TILE_ENTITY_DATA.getQuery(), getValue());
    }

    public static class Immutable extends SingleData.Immutable<TileEntityData, TileEntityDataManipulator, TileEntityDataManipulator.Immutable>
    {
        public Immutable(TileEntityData value)
        {
            super(value, MineCityKeys.TILE_ENTITY_DATA);
        }

        @Override
        public TileEntityDataManipulator asMutable()
        {
            return new TileEntityDataManipulator(getValue());
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(MineCityKeys.TILE_ENTITY_DATA.getQuery(), getValue());
        }
    }

    public static class Builder implements DataManipulatorBuilder<TileEntityDataManipulator, Immutable> {

        @Override
        public TileEntityDataManipulator create() {
            return new TileEntityDataManipulator(new SpongeTileEntityData(null, null));
        }

        @Override
        public Optional<TileEntityDataManipulator> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        public Optional<TileEntityDataManipulator> build(DataView container) throws InvalidDataException {
            return create().from(container.getContainer());
        }

    }
}
