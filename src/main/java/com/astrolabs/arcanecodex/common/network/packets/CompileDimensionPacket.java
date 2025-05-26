package com.astrolabs.arcanecodex.common.network.packets;

import com.astrolabs.arcanecodex.common.blockentities.DimensionCompilerCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompileDimensionPacket {
    private final BlockPos pos;
    private final String dimensionCode;
    
    public CompileDimensionPacket(BlockPos pos, String dimensionCode) {
        this.pos = pos;
        this.dimensionCode = dimensionCode;
    }
    
    public CompileDimensionPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.dimensionCode = buf.readUtf(2000);
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(dimensionCode, 2000);
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level().getBlockEntity(pos) instanceof DimensionCompilerCoreBlockEntity blockEntity) {
                // Verify player is close enough
                if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64) {
                    blockEntity.startDimensionCompilation(dimensionCode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}