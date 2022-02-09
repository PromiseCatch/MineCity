package br.com.gamemods.minecity.sponge.data.manipulator.reactive;

import br.com.gamemods.minecity.reactive.game.entity.ReactiveEntity;
import br.com.gamemods.minecity.reactive.game.entity.data.EntityData;
import br.com.gamemods.minecity.reactive.game.entity.data.EntityManipulator;
import br.com.gamemods.minecity.reactive.reactor.EntityReactor;
import br.com.gamemods.minecity.sponge.data.manipulator.boxed.EntityDataManipulator;
import br.com.gamemods.minecity.sponge.data.manipulator.boxed.MineCityKeys;
import br.com.gamemods.minecity.sponge.data.value.SpongeEntityData;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.Entity;

import java.util.Optional;

public class SpongeEntityManipulator implements EntityManipulator, EntityReactor
{
    private final SpongeManipulator manipulator;

    public SpongeEntityManipulator(SpongeManipulator manipulator)
    {
        this.manipulator = manipulator;
    }

    @Override
    public Optional<EntityData> getEntityData(@NotNull Object entityObj)
    {
        if(!(entityObj instanceof Entity))
            return Optional.empty();

        Entity entity = (Entity) entityObj;
        Optional<EntityData> opt = entity.get(MineCityKeys.ENTITY_DATA);
        if(opt.isPresent())
            return opt;

        SpongeEntityData data = new SpongeEntityData(manipulator, entity);
        EntityDataManipulator entityDataManipulator = new EntityDataManipulator(data);
//        DataTransactionResult result = entity.offer(entityDataManipulator);
//        if(!result.isSuccessful())
//            manipulator.sponge.logger.error("Failed to apply the entity data manipulator to the entity "+entity);

        return Optional.of(data);
    }

    @NotNull
    @Override
    public Optional<ReactiveEntity> getReactiveEntity(Object entity)
    {
        return Optional.empty();
    }

    @Override
    public String toString()
    {
        return "SpongeEntityManipulator{"+
                "manipulator="+manipulator+
                '}';
    }
}
