package br.com.gamemods.minecity.sponge.data.manipulator.boxed;

import br.com.gamemods.minecity.reactive.game.entity.data.EntityData;
import br.com.gamemods.minecity.sponge.data.value.SpongeEntityData;
import com.google.common.base.Preconditions;
import net.minecraft.network.datasync.DataSerializers;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataSerializable;
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

public class EntityDataManipulator extends AbstractSingleData<EntityData, EntityDataManipulator, EntityDataManipulator.ImmutableEntityData>
{
    public EntityDataManipulator(EntityData value)
    {
        super(MineCityKeys.ENTITY_DATA, value);
    }

    @NotNull
    @Override
    protected Value<?> getValueGetter()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MineCityKeys.ENTITY_DATA, getValue().getEntityData());
    }

    @Override
    public Optional<EntityDataManipulator> fill(DataHolder dataHolder, MergeFunction overlap)
    {
        EntityDataManipulator warpData = Preconditions.checkNotNull(overlap).merge(copy(), from(dataHolder.toContainer()).orElse(null));
        return Optional.of(set(MineCityKeys.ENTITY_DATA, warpData.get(MineCityKeys.ENTITY_DATA).get()));
    }

    @Override
    public Optional<EntityDataManipulator> from(DataContainer container)
    {
        if(container.contains(MineCityKeys.ENTITY_DATA.getQuery()))
            return Optional.of(set(MineCityKeys.ENTITY_DATA, (EntityData) Objects.requireNonNull(container.getSerializable(MineCityKeys.ENTITY_DATA.getQuery(), EntityDataManipulator.class).orElse(null))));

        return Optional.empty();
    }

    @Override
    public EntityDataManipulator copy()
    {
        return new EntityDataManipulator(getValue());
    }

    @Override
    public ImmutableEntityData asImmutable()
    {
        return new ImmutableEntityData(getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(MineCityKeys.ENTITY_DATA.getQuery(), getValue());
    }

    @Override
    public int getContentVersion()
    {
        return 1;
    }

    public static class ImmutableEntityData extends AbstractImmutableSingleData<EntityData, ImmutableEntityData, EntityDataManipulator>
    {
        public ImmutableEntityData(EntityData value)
        {
            super(MineCityKeys.ENTITY_DATA, value);
        }

        @Override
        protected ImmutableValue<?> getValueGetter()
        {
            return Sponge.getRegistry().getValueFactory().createValue(MineCityKeys.ENTITY_DATA, getValue()).asImmutable();
        }

        @Override
        public EntityDataManipulator asMutable()
        {
            return new EntityDataManipulator(getValue());
        }

        @Override
        public int getContentVersion()
        {
            return 1;
        }

    }

    public static class Builder implements DataManipulatorBuilder<EntityDataManipulator, ImmutableEntityData> {

        @Override
        public EntityDataManipulator create() {
            return new EntityDataManipulator(new SpongeEntityData(null, null));
        }

        @Override
        public Optional<EntityDataManipulator> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        public Optional<EntityDataManipulator> build(DataView container) throws InvalidDataException {
            return create().from(container.getContainer());
        }
    }
}
