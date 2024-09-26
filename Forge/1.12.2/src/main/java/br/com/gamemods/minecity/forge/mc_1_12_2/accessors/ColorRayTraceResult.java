package br.com.gamemods.minecity.forge.mc_1_12_2.accessors;

import br.com.gamemods.minecity.api.shape.Point;
import br.com.gamemods.minecity.api.shape.PrecisePoint;
import br.com.gamemods.minecity.api.world.Direction;
import br.com.gamemods.minecity.api.world.WorldDim;
import br.com.gamemods.minecity.forge.base.accessors.IRayTraceResult;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntity;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.mc_1_12_2.ColorUtil;
import br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorInterfaceTransformer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

@Referenced(at = ColorInterfaceTransformer.class)
public interface ColorRayTraceResult extends IRayTraceResult {
    @Override
    default br.com.gamemods.minecity.api.world.BlockPos getHitBlockPos(WorldDim dim) {
        BlockPos pos = ((RayTraceResult) this).getBlockPos();
        return new br.com.gamemods.minecity.api.world.BlockPos(
                dim, pos.getX(), pos.getY(), pos.getZ()
        );
    }

    @Override
    default Point getHitBlockPos() {
        BlockPos pos = ((RayTraceResult) this).getBlockPos();
        return new Point(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    default PrecisePoint getEntityPos() {
        Vec3d pos = ((RayTraceResult) this).hitVec;
        return new PrecisePoint(pos.x, pos.y, pos.z);
    }

    @Override
    default IEntity getEntity() {
        return (IEntity) ((RayTraceResult) this).entityHit;
    }

    @Override
    default int getHitType() {
        return ((RayTraceResult) this).typeOfHit.ordinal();
    }

    @Override
    default Direction getHitSide() {
        return ColorUtil.toDirection(((RayTraceResult) this).sideHit);
    }
}
