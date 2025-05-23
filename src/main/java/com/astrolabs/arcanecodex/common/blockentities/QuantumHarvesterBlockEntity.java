package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.blocks.machines.QuantumHarvesterBlock;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumHarvesterBlockEntity extends BlockEntity implements MenuProvider {
    
    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(100000);
    private final LazyOptional<IQuantumEnergy> energyOptional = LazyOptional.of(() -> energyStorage);
    
    private int harvestTicks = 0;
    private static final int HARVEST_TIME = 100; // 5 seconds
    private static final long ENERGY_PER_HARVEST = 100;
    
    public QuantumHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.QUANTUM_HARVESTER.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, QuantumHarvesterBlockEntity blockEntity) {
        boolean wasActive = state.getValue(QuantumHarvesterBlock.ACTIVE);
        boolean isActive = false;
        
        // Check if we have space for energy
        boolean hasSpace = false;
        for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
            if (blockEntity.energyStorage.getEnergyStored(type) < blockEntity.energyStorage.getMaxEnergyStored(type)) {
                hasSpace = true;
                break;
            }
        }
        
        if (hasSpace) {
            blockEntity.harvestTicks++;
            isActive = true;
            
            if (blockEntity.harvestTicks >= HARVEST_TIME) {
                blockEntity.harvestTicks = 0;
                blockEntity.harvestQuantumEnergy();
                
                // Spawn particles on harvest
                if (level.random.nextFloat() < 0.8f) {
                    level.sendParticles(
                        com.astrolabs.arcanecodex.common.particles.ModParticles.QUANTUM_ENERGY.get(),
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        5, 0.25, 0.25, 0.25, 0.05
                    );
                }
            }
        } else {
            blockEntity.harvestTicks = 0;
        }
        
        // Update block state
        if (wasActive != isActive) {
            level.setBlock(pos, state.setValue(QuantumHarvesterBlock.ACTIVE, isActive), 3);
        }
        
        // Try to output energy to adjacent blocks
        blockEntity.outputEnergy();
    }
    
    private void harvestQuantumEnergy() {
        // Harvest different types based on environment
        long skyLight = level.getBrightness(worldPosition.above());
        
        if (skyLight > 10) {
            // Daytime - harvest Coherent Light
            energyStorage.insertEnergy(IQuantumEnergy.EnergyType.COHERENT_LIGHT, ENERGY_PER_HARVEST, false);
        } else {
            // Nighttime - harvest Quantum Foam
            energyStorage.insertEnergy(IQuantumEnergy.EnergyType.QUANTUM_FOAM, ENERGY_PER_HARVEST, false);
        }
        
        // Small chance to harvest Neural Charge from nearby players
        if (level.random.nextFloat() < 0.1f) {
            Player nearestPlayer = level.getNearestPlayer(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 10, false);
            if (nearestPlayer != null) {
                energyStorage.insertEnergy(IQuantumEnergy.EnergyType.NEURAL_CHARGE, ENERGY_PER_HARVEST / 2, false);
            }
        }
        
        setChanged();
    }
    
    private void outputEnergy() {
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
            if (neighbor != null) {
                neighbor.getCapability(ModCapabilities.QUANTUM_ENERGY, direction.getOpposite()).ifPresent(storage -> {
                    for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
                        long available = energyStorage.getEnergyStored(type);
                        if (available > 0) {
                            long extracted = energyStorage.extractEnergy(type, Math.min(available, 1000), true);
                            if (extracted > 0) {
                                long accepted = storage.insertEnergy(type, extracted, false);
                                if (accepted > 0) {
                                    energyStorage.extractEnergy(type, accepted, false);
                                }
                            }
                        }
                    }
                });
            }
        }
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("Energy"));
        harvestTicks = tag.getInt("HarvestTicks");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.putInt("HarvestTicks", harvestTicks);
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
        return Component.translatable("block.arcanecodex.quantum_harvester");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        // TODO: Create GUI container
        return null;
    }
    
    public void dropContents() {
        // Quantum harvesters don't drop items, but we could add effects here
    }
    
    public QuantumEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
}