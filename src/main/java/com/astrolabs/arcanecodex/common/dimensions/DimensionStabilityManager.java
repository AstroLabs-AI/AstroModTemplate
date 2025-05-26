package com.astrolabs.arcanecodex.common.dimensions;

import com.astrolabs.arcanecodex.common.systems.reality.RealityGlitchManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber
public class DimensionStabilityManager extends SavedData {
    private static final String DATA_NAME = "arcanecodex_dimension_stability";
    private static final Random RANDOM = new Random();
    
    private final Map<ResourceLocation, DimensionStabilityData> stabilityData = new HashMap<>();
    
    private static class DimensionStabilityData {
        float stability;
        float baseInstability;
        int ticksSinceCreation;
        int playerCount;
        float energyDensity;
        
        DimensionStabilityData(float initialStability, float baseInstability, float energyDensity) {
            this.stability = initialStability;
            this.baseInstability = baseInstability;
            this.energyDensity = energyDensity;
            this.ticksSinceCreation = 0;
            this.playerCount = 0;
        }
        
        CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putFloat("stability", stability);
            tag.putFloat("baseInstability", baseInstability);
            tag.putInt("ticks", ticksSinceCreation);
            tag.putInt("players", playerCount);
            tag.putFloat("energyDensity", energyDensity);
            return tag;
        }
        
        static DimensionStabilityData load(CompoundTag tag) {
            DimensionStabilityData data = new DimensionStabilityData(
                tag.getFloat("stability"),
                tag.getFloat("baseInstability"),
                tag.getFloat("energyDensity")
            );
            data.ticksSinceCreation = tag.getInt("ticks");
            data.playerCount = tag.getInt("players");
            return data;
        }
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        
        MinecraftServer server = event.getServer();
        DimensionStabilityManager manager = get(server);
        
        // Update each dimension's stability
        for (ServerLevel level : server.getAllLevels()) {
            ResourceLocation dimId = level.dimension().location();
            
            // Only track custom dimensions
            if (!dimId.getNamespace().equals("arcanecodex") || !dimId.getPath().startsWith("custom/")) {
                continue;
            }
            
            manager.updateDimensionStability(level);
        }
        
        manager.setDirty();
    }
    
    private void updateDimensionStability(ServerLevel level) {
        ResourceLocation dimId = level.dimension().location();
        DimensionStabilityData data = stabilityData.get(dimId);
        
        if (data == null) {
            // Initialize new dimension
            var properties = DimensionManager.getDimensionProperties(dimId);
            if (properties.isPresent()) {
                data = new DimensionStabilityData(
                    1.0f - properties.get().getInstability(),
                    properties.get().getInstability(),
                    properties.get().getEnergyDensity()
                );
                stabilityData.put(dimId, data);
            } else {
                return;
            }
        }
        
        // Update player count
        data.playerCount = level.players().size();
        data.ticksSinceCreation++;
        
        // Calculate stability changes
        float stabilityChange = 0.0f;
        
        // Base decay based on instability
        stabilityChange -= data.baseInstability * 0.00001f;
        
        // Player presence affects stability
        if (data.playerCount > 0) {
            // More players = more instability
            stabilityChange -= data.playerCount * 0.00002f;
            
            // High energy density accelerates decay
            stabilityChange -= data.energyDensity * 0.00001f;
        }
        
        // Apply stability change
        data.stability = Math.max(0.0f, Math.min(1.0f, data.stability + stabilityChange));
        
        // Trigger effects based on stability
        if (data.stability < 0.3f && level.getGameTime() % 100 == 0) {
            triggerInstabilityEffects(level, data);
        }
        
        // Dimension collapse at 0 stability
        if (data.stability <= 0.0f) {
            collapseDimension(level);
        }
    }
    
    private void triggerInstabilityEffects(ServerLevel level, DimensionStabilityData data) {
        float severity = 1.0f - data.stability;
        
        // Apply effects to all players in dimension
        for (ServerPlayer player : level.players()) {
            // Visual distortion
            if (RANDOM.nextFloat() < severity) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
            }
            
            // Spatial distortion
            if (RANDOM.nextFloat() < severity * 0.5f) {
                double offsetX = (RANDOM.nextDouble() - 0.5) * severity * 2;
                double offsetZ = (RANDOM.nextDouble() - 0.5) * severity * 2;
                player.teleportTo(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ);
            }
            
            // Energy drain
            if (RANDOM.nextFloat() < severity * 0.3f) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, (int)(severity * 2)));
            }
        }
        
        // Create reality glitches
        if (RANDOM.nextFloat() < severity) {
            for (int i = 0; i < severity * 5; i++) {
                BlockPos glitchPos = new BlockPos(
                    level.random.nextInt(200) - 100,
                    level.random.nextInt(100) + 20,
                    level.random.nextInt(200) - 100
                );
                
                RealityGlitchManager.createGlitch(level, glitchPos, 
                    RealityGlitchManager.GlitchType.values()[RANDOM.nextInt(RealityGlitchManager.GlitchType.values().length)],
                    (int)(severity * 10), 200 + RANDOM.nextInt(200));
            }
        }
    }
    
    private void collapseDimension(ServerLevel level) {
        // Evacuate all players
        MinecraftServer server = level.getServer();
        ServerLevel overworld = server.overworld();
        
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§c§lDIMENSION COLLAPSING!"));
            player.teleportTo(overworld, player.getX(), 100, player.getZ(), player.getYRot(), player.getXRot());
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 2));
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
        }
        
        // Remove dimension data
        ResourceLocation dimId = level.dimension().location();
        stabilityData.remove(dimId);
        
        // Mark dimension for deletion
        DimensionManager.deleteDimension(server, dimId);
    }
    
    public float getStability(ResourceLocation dimensionId) {
        DimensionStabilityData data = stabilityData.get(dimensionId);
        return data != null ? data.stability : 1.0f;
    }
    
    public void stabilizeDimension(ResourceLocation dimensionId, float amount) {
        DimensionStabilityData data = stabilityData.get(dimensionId);
        if (data != null) {
            data.stability = Math.min(1.0f, data.stability + amount);
            setDirty();
        }
    }
    
    public Map<ResourceLocation, Float> getAllStabilities() {
        Map<ResourceLocation, Float> result = new HashMap<>();
        stabilityData.forEach((id, data) -> result.put(id, data.stability));
        return result;
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        
        for (Map.Entry<ResourceLocation, DimensionStabilityData> entry : stabilityData.entrySet()) {
            CompoundTag dimTag = new CompoundTag();
            dimTag.putString("dimension", entry.getKey().toString());
            dimTag.put("data", entry.getValue().save());
            list.add(dimTag);
        }
        
        tag.put("dimensions", list);
        return tag;
    }
    
    public static DimensionStabilityManager load(CompoundTag tag) {
        DimensionStabilityManager manager = new DimensionStabilityManager();
        
        ListTag list = tag.getList("dimensions", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag dimTag = list.getCompound(i);
            ResourceLocation dimension = new ResourceLocation(dimTag.getString("dimension"));
            DimensionStabilityData data = DimensionStabilityData.load(dimTag.getCompound("data"));
            manager.stabilityData.put(dimension, data);
        }
        
        return manager;
    }
    
    public static DimensionStabilityManager get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(
            DimensionStabilityManager::load,
            DimensionStabilityManager::new,
            DATA_NAME
        );
    }
}