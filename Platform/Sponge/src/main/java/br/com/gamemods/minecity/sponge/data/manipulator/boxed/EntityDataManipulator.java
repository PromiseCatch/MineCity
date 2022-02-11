package br.com.gamemods.minecity.sponge.data.manipulator.boxed;

import br.com.gamemods.minecity.reactive.ReactiveLayer;
import br.com.gamemods.minecity.sponge.MineCitySponge;
import br.com.gamemods.minecity.sponge.MineCitySpongePlugin;
import br.com.gamemods.minecity.sponge.data.manipulator.reactive.SpongeManipulator;
import br.com.gamemods.minecity.sponge.data.value.SpongeEntityData;
import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import scala.tools.cmd.Opt;

import java.util.Optional;

public class EntityDataManipulator extends SingleData<SpongeEntityData, EntityDataManipulator, EntityDataManipulator.ImmutableEntityData>
{
    public EntityDataManipulator(SpongeEntityData value)
    {
        super(value, MineCityKeys.ENTITY_DATA);
    }

    @NotNull
    @Override
    protected Value<?> getValueGetter()
    {
        return Sponge.getRegistry().getValueFactory().createValue(MineCityKeys.ENTITY_DATA, getValue());
    }

    @Override
    public Optional<EntityDataManipulator> fill(DataHolder dataHolder, MergeFunction overlap)
    {
        EntityDataManipulator entityData = Preconditions.checkNotNull(overlap).merge(copy(), dataHolder.get(EntityDataManipulator.class).orElse(null));
        setValue(entityData.getValue());
        return Optional.of(this);
    }

    @Override
    public Optional<EntityDataManipulator> from(DataContainer container)
    {
        if(container.contains(MineCityKeys.ENTITY_DATA.getQuery())) {
            SpongeEntityData entityData = container.getSerializable(MineCityKeys.ENTITY_DATA.getQuery(), SpongeEntityData.class).get();
            return Optional.of(setValue(entityData));
        }

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


    public static class ImmutableEntityData extends SingleData.Immutable<SpongeEntityData, EntityDataManipulator, ImmutableEntityData>
    {
        public ImmutableEntityData(SpongeEntityData value)
        {
            super(value, MineCityKeys.ENTITY_DATA );
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

        @Override
        public DataContainer toContainer() {
            return super.toContainer().set(MineCityKeys.ENTITY_DATA.getQuery(), getValue());
        }

    }

    public static class Builder implements DataManipulatorBuilder<EntityDataManipulator, ImmutableEntityData> {

        @Override
        public EntityDataManipulator create() {
            return new EntityDataManipulator(new SpongeEntityData((SpongeManipulator) ReactiveLayer.getManipulator(), null));
        }

        @Override
        public Optional<EntityDataManipulator> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        public Optional<EntityDataManipulator> build(DataView container) throws InvalidDataException {
            if (MineCitySpongePlugin.getPluginContainer() == null || true) {
                return Optional.empty();
            }
            if (!container.contains(MineCityKeys.ENTITY_DATA)) {
                return Optional.empty();
            }
            SpongeEntityData data = container.getSerializable(MineCityKeys.ENTITY_DATA.getQuery(), SpongeEntityData.class).get();
            return Optional.of(new EntityDataManipulator(data));
        }
    }
}
