package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.blocks.machines.NeuralInterfaceBlock;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import com.astrolabs.arcanecodex.client.gui.holographic.ResearchTreeMenu;
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

import java.util.UUID;

public class NeuralInterfaceBlockEntity extends BlockEntity implements MenuProvider {
    
    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(50000);
    private final LazyOptional<IQuantumEnergy> energyOptional = LazyOptional.of(() -> energyStorage);
    
    private UUID linkedPlayer = null;
    private int neuralChargeBuffer = 0;
    private static final int NEURAL_CHARGE_RATE = 10; // per second
    private static final long ENERGY_COST_PER_CHARGE = 50;
    
    public NeuralInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEURAL_INTERFACE.get(), pos, state);
    }
    
    public void linkToPlayer(Player player) {
        this.linkedPlayer = player.getUUID();
        level.setBlock(worldPosition, getBlockState().setValue(NeuralInterfaceBlock.LINKED, true), 3);
        setChanged();
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, NeuralInterfaceBlockEntity blockEntity) {
        if (blockEntity.linkedPlayer != null && level.getGameTime() % 20 == 0) { // Every second
            Player player = level.getServer().getPlayerList().getPlayer(blockEntity.linkedPlayer);
            if (player != null && player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 100) { // 10 block range
                blockEntity.processNeuralCharge(player);
            }
        }
    }
    
    public static void clientTick(Level level, BlockPos pos, BlockState state, NeuralInterfaceBlockEntity blockEntity) {
        // Add particle effects for linked state
        if (state.getValue(NeuralInterfaceBlock.LINKED) && level.random.nextFloat() < 0.1f) {
            // TODO: Add holographic particles
        }
    }
    
    private void processNeuralCharge(Player player) {
        player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
            // Convert quantum energy to neural charge
            long energyAvailable = energyStorage.getEnergyStored(IQuantumEnergy.EnergyType.NEURAL_CHARGE);
            if (energyAvailable >= ENERGY_COST_PER_CHARGE) {
                long extracted = energyStorage.extractEnergy(IQuantumEnergy.EnergyType.NEURAL_CHARGE, ENERGY_COST_PER_CHARGE, false);
                if (extracted > 0) {
                    consciousness.addNeuralCharge(NEURAL_CHARGE_RATE);
                    neuralChargeBuffer += NEURAL_CHARGE_RATE;
                    
                    // Level up consciousness based on usage
                    if (neuralChargeBuffer >= 1000) {
                        neuralChargeBuffer = 0;
                        consciousness.setConsciousnessLevel(consciousness.getConsciousnessLevel() + 1);
                        player.sendSystemMessage(Component.literal("Consciousness expanded! Level: " + consciousness.getConsciousnessLevel())
                            .withStyle(style -> style.withColor(0x9932CC)));
                    }
                }
            }
            
            // Also try other energy types with lower efficiency
            for (IQuantumEnergy.EnergyType type : IQuantumEnergy.EnergyType.values()) {
                if (type != IQuantumEnergy.EnergyType.NEURAL_CHARGE) {
                    long available = energyStorage.getEnergyStored(type);
                    if (available >= ENERGY_COST_PER_CHARGE * 2) {
                        long extracted = energyStorage.extractEnergy(type, ENERGY_COST_PER_CHARGE * 2, false);
                        if (extracted > 0) {
                            consciousness.addNeuralCharge(NEURAL_CHARGE_RATE / 2);
                        }
                    }
                }
            }
        });
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("Energy"));
        if (tag.hasUUID("LinkedPlayer")) {
            linkedPlayer = tag.getUUID("LinkedPlayer");
        }
        neuralChargeBuffer = tag.getInt("NeuralBuffer");
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        if (linkedPlayer != null) {
            tag.putUUID("LinkedPlayer", linkedPlayer);
        }
        tag.putInt("NeuralBuffer", neuralChargeBuffer);
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
        return Component.translatable("block.arcanecodex.neural_interface");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ResearchTreeMenu(id, inventory);
    }
    
    public QuantumEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    
    public UUID getLinkedPlayer() {
        return linkedPlayer;
    }
}