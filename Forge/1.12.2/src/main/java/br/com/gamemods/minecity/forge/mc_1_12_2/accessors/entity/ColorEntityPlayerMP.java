package br.com.gamemods.minecity.forge.mc_1_12_2.accessors.entity;

import br.com.gamemods.minecity.api.command.Message;
import br.com.gamemods.minecity.forge.base.MineCityForge;
import br.com.gamemods.minecity.forge.base.accessors.block.IState;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntity;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityLiving;
import br.com.gamemods.minecity.forge.base.accessors.entity.base.IEntityPlayerMP;
import br.com.gamemods.minecity.forge.base.core.Referenced;
import br.com.gamemods.minecity.forge.mc_1_12_2.core.transformer.forge.ColorEntityPlayerMPTransformer;
import io.netty.buffer.Unpooled;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

@Referenced(at = ColorEntityPlayerMPTransformer.class)
public interface ColorEntityPlayerMP extends IEntityPlayerMP, ColorEntity {
    @Override
    default void sendBlock(int x, int y, int z) {
        sendPacket(new SPacketBlockChange(getWorld(), new BlockPos(x, y, z)));
    }

    @Override
    default void sendFakeBlock(int x, int y, int z, IState state) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer(8 + 4));
        buffer.writeBlockPos(new BlockPos(x, y, z));
        buffer.writeVarInt(0);

        try {
            SPacketBlockChange packet = new SPacketBlockChange();
            packet.readPacketData(buffer);

            packet.blockState = (IBlockState) state;
            sendPacket(packet);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    default void sendTitle(MineCityForge mod, Message title, Message subtitle) {
        TextComponentString empty = new TextComponentString("");
        sendPacket(new SPacketTitle(SPacketTitle.Type.RESET, empty));
        sendPacket(new SPacketTitle(SPacketTitle.Type.TIMES, empty, 10, 70, 20));
        if(title != null) {
            sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, ITextComponent.Serializer.jsonToComponent(
                    mod.transformer.toJson(title)
            )));
        } else {
            sendPacket(new SPacketTitle(SPacketTitle.Type.TITLE, empty));
        }

        if(subtitle != null) {
            sendPacket(new SPacketTitle(SPacketTitle.Type.SUBTITLE, ITextComponent.Serializer.jsonToComponent(
                    mod.transformer.toJson(subtitle)
            )));
        }
    }

    @Override
    default void sendTileEntity(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tile = getWorld().getTileEntity(pos);
        if(tile == null) return;

        Packet packet = tile.getUpdatePacket();
        if(packet == null) {
            NBTTagCompound nbt = tile.serializeNBT();
            packet = new SPacketUpdateTileEntity(pos, 1, nbt);
        }

        sendPacket(packet);
    }

    @Override
    default void sendFakeAir(int x, int y, int z) {
        sendFakeBlock(x, y, z, (IState) Blocks.AIR.getDefaultState());
    }

    @Override
    default void sendHealth() {
        EntityPlayerMP player = (EntityPlayerMP) this;
        sendPacket(new SPacketUpdateHealth(player.getHealth(), player.getFoodStats().getFoodLevel(), player.getFoodStats().getSaturationLevel()));
    }

    @Override
    default void sendLeashState(IEntityLiving entity) {
        sendPacket(new SPacketEntityAttach((Entity)entity, (Entity)entity.getLeashHolder()));
    }

    @Override
    default void sendTeleport(IEntity entity) {
        sendPacket(new SPacketEntityTeleport((Entity) entity));
    }
}
