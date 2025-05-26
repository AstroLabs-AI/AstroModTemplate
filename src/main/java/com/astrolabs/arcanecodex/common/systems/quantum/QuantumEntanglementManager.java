package com.astrolabs.arcanecodex.common.systems.quantum;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;

public class QuantumEntanglementManager extends SavedData {
    
    private static final String DATA_NAME = "arcanecodex_quantum_entanglements";
    private final Map<UUID, EntangledPair> entanglements = new HashMap<>();
    private final Map<BlockPos, UUID> positionIndex = new HashMap<>();
    
    public static class EntangledPair {
        public final UUID id;
        public final BlockPos pos1;
        public final BlockPos pos2;
        public final long creationTime;
        public double coherence = 1.0; // Degrades over distance and time
        
        public EntangledPair(UUID id, BlockPos pos1, BlockPos pos2, long creationTime) {
            this.id = id;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.creationTime = creationTime;
        }
        
        public double getDistance() {
            return Math.sqrt(pos1.distSqr(pos2));
        }
        
        public void updateCoherence(long currentTime) {
            // Coherence degrades with time and distance
            long age = currentTime - creationTime;
            double timeFactor = Math.exp(-age / 72000.0); // Half-life of 1 hour
            double distanceFactor = 1.0 / (1.0 + getDistance() / 100.0);
            coherence = timeFactor * distanceFactor;
        }
    }
    
    public QuantumEntanglementManager() {
    }
    
    public static QuantumEntanglementManager get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(
            QuantumEntanglementManager::load,
            QuantumEntanglementManager::new,
            DATA_NAME
        );
    }
    
    public static UUID createEntanglement(Level level, BlockPos pos1, BlockPos pos2) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }
        
        QuantumEntanglementManager manager = get(serverLevel);
        
        // Check if either position is already entangled
        if (manager.positionIndex.containsKey(pos1) || manager.positionIndex.containsKey(pos2)) {
            // Break existing entanglement
            manager.breakEntanglement(pos1);
            manager.breakEntanglement(pos2);
        }
        
        // Create new entanglement
        UUID pairId = UUID.randomUUID();
        EntangledPair pair = new EntangledPair(pairId, pos1, pos2, level.getGameTime());
        
        manager.entanglements.put(pairId, pair);
        manager.positionIndex.put(pos1, pairId);
        manager.positionIndex.put(pos2, pairId);
        
        manager.setDirty();
        
        return pairId;
    }
    
    public void breakEntanglement(BlockPos pos) {
        UUID pairId = positionIndex.get(pos);
        if (pairId != null) {
            EntangledPair pair = entanglements.remove(pairId);
            if (pair != null) {
                positionIndex.remove(pair.pos1);
                positionIndex.remove(pair.pos2);
                setDirty();
            }
        }
    }
    
    public EntangledPair getEntanglement(BlockPos pos) {
        UUID pairId = positionIndex.get(pos);
        return pairId != null ? entanglements.get(pairId) : null;
    }
    
    public BlockPos getEntangledPosition(BlockPos pos) {
        EntangledPair pair = getEntanglement(pos);
        if (pair != null) {
            return pair.pos1.equals(pos) ? pair.pos2 : pair.pos1;
        }
        return null;
    }
    
    public void tickEntanglements(ServerLevel level) {
        long currentTime = level.getGameTime();
        Iterator<Map.Entry<UUID, EntangledPair>> iterator = entanglements.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<UUID, EntangledPair> entry = iterator.next();
            EntangledPair pair = entry.getValue();
            
            pair.updateCoherence(currentTime);
            
            // Remove if coherence is too low
            if (pair.coherence < 0.01) {
                positionIndex.remove(pair.pos1);
                positionIndex.remove(pair.pos2);
                iterator.remove();
                setDirty();
                
                // Spawn breaking particles
                spawnBreakingParticles(level, pair);
            }
        }
    }
    
    private void spawnBreakingParticles(ServerLevel level, EntangledPair pair) {
        // Spawn particles at both positions
        for (int i = 0; i < 10; i++) {
            level.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.REALITY_GLITCH.get(),
                pair.pos1.getX() + 0.5, pair.pos1.getY() + 0.5, pair.pos1.getZ() + 0.5,
                1, 0.5, 0.5, 0.5, 0.1
            );
            level.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.REALITY_GLITCH.get(),
                pair.pos2.getX() + 0.5, pair.pos2.getY() + 0.5, pair.pos2.getZ() + 0.5,
                1, 0.5, 0.5, 0.5, 0.1
            );
        }
    }
    
    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag entanglementList = new ListTag();
        
        for (EntangledPair pair : entanglements.values()) {
            CompoundTag pairTag = new CompoundTag();
            pairTag.putUUID("Id", pair.id);
            pairTag.put("Pos1", NbtUtils.writeBlockPos(pair.pos1));
            pairTag.put("Pos2", NbtUtils.writeBlockPos(pair.pos2));
            pairTag.putLong("CreationTime", pair.creationTime);
            pairTag.putDouble("Coherence", pair.coherence);
            entanglementList.add(pairTag);
        }
        
        tag.put("Entanglements", entanglementList);
        return tag;
    }
    
    public static QuantumEntanglementManager load(CompoundTag tag) {
        QuantumEntanglementManager manager = new QuantumEntanglementManager();
        
        ListTag entanglementList = tag.getList("Entanglements", Tag.TAG_COMPOUND);
        for (int i = 0; i < entanglementList.size(); i++) {
            CompoundTag pairTag = entanglementList.getCompound(i);
            
            UUID id = pairTag.getUUID("Id");
            BlockPos pos1 = NbtUtils.readBlockPos(pairTag.getCompound("Pos1"));
            BlockPos pos2 = NbtUtils.readBlockPos(pairTag.getCompound("Pos2"));
            long creationTime = pairTag.getLong("CreationTime");
            
            EntangledPair pair = new EntangledPair(id, pos1, pos2, creationTime);
            pair.coherence = pairTag.getDouble("Coherence");
            
            manager.entanglements.put(id, pair);
            manager.positionIndex.put(pos1, id);
            manager.positionIndex.put(pos2, id);
        }
        
        return manager;
    }
}