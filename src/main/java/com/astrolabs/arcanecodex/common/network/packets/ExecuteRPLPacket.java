package com.astrolabs.arcanecodex.common.network.packets;

import com.astrolabs.arcanecodex.common.blockentities.RealityCompilerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ExecuteRPLPacket {
    
    private final BlockPos pos;
    private final List<String> codeLines;
    
    public ExecuteRPLPacket(BlockPos pos, List<String> codeLines) {
        this.pos = pos;
        this.codeLines = codeLines;
    }
    
    public ExecuteRPLPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        int lineCount = buf.readInt();
        this.codeLines = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            codeLines.add(buf.readUtf());
        }
    }
    
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(codeLines.size());
        for (String line : codeLines) {
            buf.writeUtf(line);
        }
    }
    
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.level().isLoaded(pos)) {
                BlockEntity be = player.level().getBlockEntity(pos);
                if (be instanceof RealityCompilerBlockEntity compiler) {
                    // Update code and start execution
                    compiler.getCodeLines().clear();
                    compiler.getCodeLines().addAll(codeLines);
                    compiler.startExecution(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}