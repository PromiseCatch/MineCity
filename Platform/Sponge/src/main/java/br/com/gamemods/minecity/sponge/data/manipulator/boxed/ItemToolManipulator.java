package br.com.gamemods.minecity.sponge.data.manipulator.boxed;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Objects;
import java.util.Optional;

public class ItemToolManipulator extends AbstractSingleData<Integer, ItemToolManipulator, ItemToolManipulator.Immutable>
{
    public ItemToolManipulator(Integer value)
    {
        super(MineCityKeys.ITEM_TOOL, value);
    }

    @NotNull
    @Override
    protected Value<?> getValueGetter()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MineCityKeys.ITEM_TOOL, getValue());
    }

    @Override
    public Optional<ItemToolManipulator> fill(DataHolder dataHolder, MergeFunction overlap)
    {
        ItemToolManipulator warpData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
        return Optional.of(set(MineCityKeys.ITEM_TOOL, warpData.get(MineCityKeys.ITEM_TOOL).get()));
    }

    @Override
    public Optional<ItemToolManipulator> from(DataContainer container)
    {
        if(container.contains(MineCityKeys.ITEM_TOOL.getQuery()))
            return Optional.of(set(MineCityKeys.ITEM_TOOL, (Integer) Objects.requireNonNull(container.get(MineCityKeys.ITEM_TOOL.getQuery()).orElse(null))));

        return Optional.empty();
    }

    @Override
    public ItemToolManipulator copy()
    {
        return new ItemToolManipulator(getValue());
    }

    @Override
    public Immutable asImmutable()
    {
        return new Immutable(getValue());
    }

    @Override
    public int getContentVersion()
    {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(MineCityKeys.ITEM_TOOL, getValue());
    }

    public static class Immutable extends AbstractImmutableSingleData<Integer, Immutable, ItemToolManipulator>
    {
        public Immutable(Integer value)
        {
            super(MineCityKeys.ITEM_TOOL, value);
        }

        @Override
        protected ImmutableValue<?> getValueGetter()
        {
            return Sponge.getRegistry().getValueFactory().createValue(MineCityKeys.ITEM_TOOL, getValue()).asImmutable();
        }

        @Override
        public ItemToolManipulator asMutable()
        {
            return new ItemToolManipulator(getValue());
        }

        @Override
        public int getContentVersion()
        {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(MineCityKeys.ITEM_TOOL, getValue());
        }
    }

    public static class Builder implements DataManipulatorBuilder<ItemToolManipulator, Immutable> {

        @Override
        public ItemToolManipulator create() {
            return new ItemToolManipulator(0);
        }

        @Override
        public Optional<ItemToolManipulator> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        public Optional<ItemToolManipulator> build(DataView container) throws InvalidDataException {
            return create().from(container.getContainer());
        }
    }
}
