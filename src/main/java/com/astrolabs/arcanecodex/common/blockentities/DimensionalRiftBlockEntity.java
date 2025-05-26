package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class DimensionalRiftBlockEntity extends BlockEntity {
    private ResourceKey<Level> destinationDimension;
    private Vec3 destinationPos;
    private float stability = 1.0f;
    private int ticksExisted = 0;
    
    public DimensionalRiftBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIMENSIONAL_RIFT.get(), pos, state);
        this.destinationPos = new Vec3(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }
    
    public void setDestination(ResourceKey<Level> dimension, Vec3 pos) {
        this.destinationDimension = dimension;
        this.destinationPos = pos;
        setChanged();
    }
    
    public void teleportEntity(Entity entity) {
        if (level == null || level.isClientSide || destinationDimension == null) {
            return;
        }
        
        ServerLevel server = (ServerLevel) level;
        ServerLevel destination = server.getServer().getLevel(destinationDimension);
        
        if (destination == null) {
            if (entity instanceof Player player) {
                player.displayClientMessage(Component.literal("§cDestination dimension not found!"), true);
            }
            return;
        }
        
        // Apply teleportation cooldown
        if (entity.isOnPortalCooldown()) {
            return;
        }
        
        // Teleport the entity
        if (entity instanceof ServerPlayer player) {
            player.teleportTo(destination, destinationPos.x, destinationPos.y, destinationPos.z, 
                player.getYRot(), player.getXRot());
            player.setPortalCooldown();
            
            // Apply dimensional effects
            applyDimensionalEffects(player);
        } else {
            entity.changeDimension(destination);
            entity.teleportTo(destinationPos.x, destinationPos.y, destinationPos.z);
            entity.setPortalCooldown();
        }
        
        // Reduce stability
        stability -= 0.1f;
        if (stability <= 0) {
            collapseRift();
        }
    }
    
    private void applyDimensionalEffects(ServerPlayer player) {
        // Add visual/audio effects when traveling through rift
        player.playSound(net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 1.0f, 1.0f);
        
        // Add temporary disorientation effect
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(
            net.minecraft.world.effect.MobEffects.CONFUSION, 100, 0));
    }
    
    public void showDestinationInfo(Player player) {
        if (destinationDimension != null) {
            String dimName = destinationDimension.location().toString();
            player.displayClientMessage(Component.literal("§bDestination: §f" + dimName), false);
            player.displayClientMessage(Component.literal("§bCoordinates: §f" + 
                String.format("%.1f, %.1f, %.1f", destinationPos.x, destinationPos.y, destinationPos.z)), false);
            player.displayClientMessage(Component.literal("§bStability: §f" + 
                String.format("%.0f%%", stability * 100)), false);
        }
    }
    
    public static void tick(Level level, BlockPos pos, BlockState state, DimensionalRiftBlockEntity blockEntity) {
        blockEntity.ticksExisted++;
        
        // Slowly decay stability
        if (blockEntity.ticksExisted % 20 == 0) {
            blockEntity.stability -= 0.001f;
            if (blockEntity.stability <= 0) {
                blockEntity.collapseRift();
            }
        }
        
        // Create ambient effects
        if (level.isClientSide && blockEntity.ticksExisted % 5 == 0) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            
            // Particle effects handled by block
        }
    }
    
    private void collapseRift() {
        if (level != null && !level.isClientSide) {
            // Create explosion effect
            level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, 
                worldPosition.getZ() + 0.5, 2.0f, Level.ExplosionInteraction.NONE);
            
            // Remove the rift
            level.removeBlock(worldPosition, false);
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        if (destinationDimension != null) {
            tag.putString("destination", destinationDimension.location().toString());
        }
        
        tag.putDouble("destX", destinationPos.x);
        tag.putDouble("destY", destinationPos.y);
        tag.putDouble("destZ", destinationPos.z);
        tag.putFloat("stability", stability);
        tag.putInt("ticksExisted", ticksExisted);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        
        if (tag.contains("destination")) {
            ResourceLocation dimLoc = new ResourceLocation(tag.getString("destination"));
            destinationDimension = ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
        }
        
        destinationPos = new Vec3(
            tag.getDouble("destX"),
            tag.getDouble("destY"),
            tag.getDouble("destZ")
        );
        
        stability = tag.getFloat("stability");
        ticksExisted = tag.getInt("ticksExisted");
    }
}