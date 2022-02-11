package br.com.gamemods.minecity.reactive.game.entity;

import br.com.gamemods.minecity.api.world.EntityPos;
import br.com.gamemods.minecity.api.world.MinecraftEntity;
import br.com.gamemods.minecity.reactive.game.block.Interaction;
import br.com.gamemods.minecity.reactive.game.entity.data.EntityData;
import br.com.gamemods.minecity.reactive.reaction.Reaction;
import org.jetbrains.annotations.NotNull;

//TODO: Implement
public final class ReactiveEntity
{

    @NotNull
    private final EntityData entityData;

    public ReactiveEntity(EntityData entityData)
    {
        this.entityData = entityData;
    }

    public Reaction reactSpawn(MinecraftEntity entity, EntityPos pos) {
        return null;
    }

    public Reaction rightClick(Interaction event) {
        if (event.getEntity() != this.entityData) throw new IllegalArgumentException("Event entity is not this entity");

        return null;
    }
}
