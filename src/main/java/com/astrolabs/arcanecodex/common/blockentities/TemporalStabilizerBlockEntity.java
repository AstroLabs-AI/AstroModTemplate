package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.blocks.machines.TemporalStabilizerBlock;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.common.particles.ModParticles;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import com.astrolabs.arcanecodex.common.systems.temporal.TemporalEcho;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TemporalStabilizerBlockEntity extends BlockEntity implements MenuProvider {
    
    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(100000);
    private final LazyOptional<IQuantumEnergy> energyOptional = LazyOptional.of(() -> energyStorage);
    
    private float temporalField = 0.0f; // 0.0 = normal time, -1.0 = frozen, 1.0 = double speed
    private int fieldRadius = 8;
    private long chronocharge = 0;
    private static final long MAX_CHRONOCHARGE = 10000;
    
    private final List<TemporalEcho> temporalEchoes = new ArrayList<>();
    private static final int MAX_ECHOES = 5;
    
    public TemporalStabilizerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TEMPORAL_STABILIZER.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, TemporalStabilizerBlockEntity blockEntity) {
        boolean powered = state.getValue(TemporalStabilizerBlock.POWERED);
        boolean active = state.getValue(TemporalStabilizerBlock.ACTIVE);
        
        if (powered && blockEntity.hasEnoughEnergy()) {
            blockEntity.temporalField = Math.min(1.0f, blockEntity.temporalField + 0.02f);
            blockEntity.consumeEnergy();
            
            // Apply temporal effects
            blockEntity.applyTemporalField(level);
            
            // Generate chronocharge
            if (level.getGameTime() % 20 == 0) {
                blockEntity.chronocharge = Math.min(MAX_CHRONOCHARGE, blockEntity.chronocharge + 10);
            }
            
            // Record temporal echoes
            if (level.getGameTime() % 40 == 0) {
                blockEntity.recordTemporalEchoes(level);
            }
            
            if (!active) {
                level.setBlock(pos, state.setValue(TemporalStabilizerBlock.ACTIVE, true), 3);
            }
        } else {
            blockEntity.temporalField = Math.max(0.0f, blockEntity.temporalField - 0.05f);
            if (active && blockEntity.temporalField <= 0) {
                level.setBlock(pos, state.setValue(TemporalStabilizerBlock.ACTIVE, false), 3);
            }
        }
        
        // Process echoes
        blockEntity.processTemporalEchoes(level);
    }
    
    public static void clientTick(Level level, BlockPos pos, BlockState state, TemporalStabilizerBlockEntity blockEntity) {
        if (state.getValue(TemporalStabilizerBlock.ACTIVE)) {
            // Spawn temporal particles
            if (level.random.nextFloat() < 0.3f) {
                double angle = level.random.nextDouble() * Math.PI * 2;
                double radius = blockEntity.fieldRadius * level.random.nextDouble();
                
                double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
                double y = pos.getY() + 0.5 + level.random.nextDouble() * 3;
                double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
                
                level.addParticle(
                    ModParticles.QUANTUM_ENERGY.get(),
                    x, y, z,
                    0, 0.02, 0
                );
            }
        }
    }
    
    private boolean hasEnoughEnergy() {
        return energyStorage.getEnergyStored(IQuantumEnergy.EnergyType.TEMPORAL_FLUX) >= 100;
    }
    
    private void consumeEnergy() {
        energyStorage.extractEnergy(IQuantumEnergy.EnergyType.TEMPORAL_FLUX, 100, false);
    }
    
    private void applyTemporalField(Level level) {
        AABB area = new AABB(worldPosition).inflate(fieldRadius);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, area);
        
        for (Entity entity : entities) {
            double distance = entity.position().distanceTo(Vec3.atCenterOf(worldPosition));
            double fieldStrength = 1.0 - (distance / fieldRadius);
            
            if (fieldStrength > 0) {
                // Apply time dilation/acceleration
                Vec3 motion = entity.getDeltaMovement();
                double timeFactor = 1.0 + (temporalField * fieldStrength);
                
                if (Math.abs(timeFactor - 1.0) > 0.01) {
                    entity.setDeltaMovement(motion.scale(timeFactor));
                    
                    // Age entities faster/slower
                    if (entity instanceof LivingEntity living && level.getGameTime() % 20 == 0) {
                        if (timeFactor > 1.5) {
                            // Accelerated aging
                            living.setHealth(living.getHealth() - 0.5f);
                        } else if (timeFactor < 0.5) {
                            // Temporal stasis - heal slowly
                            living.heal(0.5f);
                        }
                    }
                }
            }
        }
    }
    
    private void recordTemporalEchoes(Level level) {
        if (chronocharge < 1000) return;
        
        AABB area = new AABB(worldPosition).inflate(fieldRadius / 2);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        
        for (LivingEntity entity : entities) {
            if (temporalEchoes.size() >= MAX_ECHOES) {
                temporalEchoes.remove(0);
            }
            
            TemporalEcho echo = new TemporalEcho(entity, level.getGameTime());
            temporalEchoes.add(echo);
            
            // Consume chronocharge
            chronocharge -= 200;
            if (chronocharge <= 0) break;
        }
    }
    
    private void processTemporalEchoes(Level level) {
        Iterator<TemporalEcho> iterator = temporalEchoes.iterator();
        
        while (iterator.hasNext()) {
            TemporalEcho echo = iterator.next();
            
            if (!echo.tick(level)) {
                iterator.remove();
            }
        }
    }
    
    public void createTemporalRewind(Player player) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        
        if (chronocharge >= 5000) {
            // Create powerful temporal rewind effect
            AABB area = new AABB(worldPosition).inflate(fieldRadius);
            List<Entity> entities = serverLevel.getEntitiesOfClass(Entity.class, area);
            
            for (Entity entity : entities) {
                // Store current position and restore it after a delay
                Vec3 currentPos = entity.position();
                
                // Visual effect
                serverLevel.sendParticles(
                    ModParticles.HOLOGRAPHIC.get(),
                    currentPos.x, currentPos.y + 1, currentPos.z,
                    50, 0.5, 1, 0.5, 0.1
                );
            }
            
            chronocharge -= 5000;
            player.sendSystemMessage(Component.literal("Temporal rewind initiated!")
                .withStyle(style -> style.withColor(0x00FFFF)));
        } else {
            player.sendSystemMessage(Component.literal("Insufficient chronocharge!")
                .withStyle(style -> style.withColor(0xFF0000)));
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("Energy"));
        temporalField = tag.getFloat("TemporalField");
        fieldRadius = tag.getInt("FieldRadius");
        chronocharge = tag.getLong("Chronocharge");
        
        temporalEchoes.clear();
        ListTag echoList = tag.getList("TemporalEchoes", Tag.TAG_COMPOUND);
        for (int i = 0; i < echoList.size(); i++) {
            temporalEchoes.add(TemporalEcho.load(echoList.getCompound(i)));
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.putFloat("TemporalField", temporalField);
        tag.putInt("FieldRadius", fieldRadius);
        tag.putLong("Chronocharge", chronocharge);
        
        ListTag echoList = new ListTag();
        for (TemporalEcho echo : temporalEchoes) {
            echoList.add(echo.save());
        }
        tag.put("TemporalEchoes", echoList);
    }
    
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ModCapabilities.QUANTUM_ENERGY) {
            return energyOptional.cast();
        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyOptional.invalidate();
    }
    
    @Override
    public Component getDisplayName() {
        return Component.translatable("block.arcanecodex.temporal_stabilizer");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return null; // TODO: Create GUI if needed
    }
    
    public long getChronocharge() {
        return chronocharge;
    }
    
    public float getTemporalField() {
        return temporalField;
    }
}