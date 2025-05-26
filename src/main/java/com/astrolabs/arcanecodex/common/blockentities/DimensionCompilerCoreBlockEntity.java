package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import com.astrolabs.arcanecodex.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DimensionCompilerCoreBlockEntity extends BlockEntity implements MenuProvider, Container {
    private static final long ENERGY_CAPACITY = 100000L;
    private static final int MULTIBLOCK_SIZE = 7; // 7x7x7 structure

    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(ENERGY_CAPACITY);
    private final LazyOptional<QuantumEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    private boolean multiblockFormed = false;
    private String dimensionCode = "";
    private float stability = 0.0f;
    private int compilationProgress = 0;
    private int compilationTime = 0;

    public DimensionCompilerCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DIMENSION_COMPILER_CORE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DimensionCompilerCoreBlockEntity blockEntity) {
        if (level.getGameTime() % 20 == 0) {
            blockEntity.checkMultiblockStructure();
        }

        if (blockEntity.multiblockFormed && blockEntity.compilationTime > 0) {
            long energyRequired = 100L;
            // Use quantum foam for dimension compilation
            if (blockEntity.energyStorage.extractEnergy(IQuantumEnergy.EnergyType.QUANTUM_FOAM, energyRequired, true) >= energyRequired) {
                blockEntity.energyStorage.extractEnergy(IQuantumEnergy.EnergyType.QUANTUM_FOAM, energyRequired, false);
                blockEntity.compilationProgress++;

                if (blockEntity.compilationProgress >= blockEntity.compilationTime) {
                    blockEntity.completeDimensionCompilation();
                }

                blockEntity.setChanged();
            }
        }

        if (blockEntity.multiblockFormed && blockEntity.stability > 0) {
            blockEntity.stability -= 0.001f;
            if (blockEntity.stability < 0) {
                blockEntity.stability = 0;
            }
            blockEntity.setChanged();
        }
    }

    private void checkMultiblockStructure() {
        BlockPos center = worldPosition;
        boolean formed = true;
        List<BlockPos> checkedPositions = new ArrayList<>();

        // Check 7x7x7 structure
        for (int x = -3; x <= 3; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) continue; // Skip center (this block)

                    BlockPos checkPos = center.offset(x, y, z);
                    BlockState blockState = level.getBlockState(checkPos);

                    // Check structure pattern
                    boolean isFrame = Math.abs(x) == 3 || Math.abs(y) == 3 || Math.abs(z) == 3;
                    boolean isStabilizer = (Math.abs(x) == 2 && Math.abs(y) == 2 && Math.abs(z) == 2);

                    if (isFrame) {
                        if (!blockState.is(ModBlocks.DIMENSION_FRAME.get())) {
                            formed = false;
                            break;
                        }
                    } else if (isStabilizer) {
                        if (!blockState.is(ModBlocks.DIMENSION_STABILIZER.get())) {
                            formed = false;
                            break;
                        }
                    } else {
                        // Interior must be air
                        if (!blockState.isAir()) {
                            formed = false;
                            break;
                        }
                    }

                    checkedPositions.add(checkPos);
                }
                if (!formed) break;
            }
            if (!formed) break;
        }

        if (formed != multiblockFormed) {
            multiblockFormed = formed;
            if (formed) {
                stability = 1.0f;
                // Log dimension compiler formation
            } else {
                stability = 0.0f;
                compilationProgress = 0;
                compilationTime = 0;
                // Log dimension compiler destruction
            }
            setChanged();
        }
    }

    public void startDimensionCompilation(String code) {
        if (!multiblockFormed || compilationTime > 0) {
            return;
        }

        dimensionCode = code;
        compilationTime = calculateCompilationTime(code);
        compilationProgress = 0;
        setChanged();
    }

    private int calculateCompilationTime(String code) {
        // Base time + additional time based on code complexity
        return 200 + code.length() * 2;
    }

    private void completeDimensionCompilation() {
        // Dimension compilation complete
        if (level != null && !level.isClientSide && level.getServer() != null) {
            // Parse the dimension code
            var parseResult = com.astrolabs.arcanecodex.common.dimensions.DimensionCodeParser.parse(dimensionCode);
            
            if (parseResult.success && parseResult.properties != null) {
                // Create the dimension
                var creationResult = com.astrolabs.arcanecodex.common.dimensions.DimensionManager.createDimension(
                    level.getServer(), parseResult.properties);
                
                if (creationResult.success) {
                    // Consume the energy cost
                    var validationResult = com.astrolabs.arcanecodex.common.dimensions.DimensionValidator.validate(parseResult.properties);
                    energyStorage.extractEnergy(IQuantumEnergy.EnergyType.QUANTUM_FOAM, validationResult.energyCost, false);
                    
                    // Create a dimensional rift above the compiler
                    createDimensionalRift(creationResult.dimensionKey);
                }
            }
        }
        
        compilationProgress = 0;
        compilationTime = 0;
        dimensionCode = "";
        setChanged();
    }

    public boolean isMultiblockFormed() {
        return multiblockFormed;
    }

    public float getStability() {
        return stability;
    }

    public int getCompilationProgress() {
        return compilationProgress;
    }

    public int getCompilationTime() {
        return compilationTime;
    }

    public String getDimensionCode() {
        return dimensionCode;
    }

    public int getEnergyStored() {
        return (int) energyStorage.getEnergyStored(IQuantumEnergy.EnergyType.QUANTUM_FOAM);
    }

    public int getMaxEnergyStored() {
        return (int) energyStorage.getMaxEnergyStored(IQuantumEnergy.EnergyType.QUANTUM_FOAM);
    }
    
    private void createDimensionalRift(net.minecraft.resources.ResourceKey<Level> dimensionKey) {
        if (level == null || level.isClientSide) return;
        
        // Find a suitable position for the rift (3 blocks above the compiler core)
        BlockPos riftPos = worldPosition.above(3);
        
        // Clear the space
        for (int y = 0; y < 3; y++) {
            BlockPos checkPos = worldPosition.above(y + 1);
            if (!level.getBlockState(checkPos).isAir()) {
                level.destroyBlock(checkPos, true);
            }
        }
        
        // Place the dimensional rift
        level.setBlock(riftPos, ModBlocks.DIMENSIONAL_RIFT.get().defaultBlockState(), 3);
        
        // Configure the rift
        if (level.getBlockEntity(riftPos) instanceof DimensionalRiftBlockEntity rift) {
            // Set destination to spawn point of new dimension
            rift.setDestination(dimensionKey, new net.minecraft.world.phys.Vec3(0.5, 64, 0.5));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("energy", energyStorage.serializeNBT());
        tag.putBoolean("multiblockFormed", multiblockFormed);
        tag.putString("dimensionCode", dimensionCode);
        tag.putFloat("stability", stability);
        tag.putInt("compilationProgress", compilationProgress);
        tag.putInt("compilationTime", compilationTime);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        multiblockFormed = tag.getBoolean("multiblockFormed");
        dimensionCode = tag.getString("dimensionCode");
        stability = tag.getFloat("stability");
        compilationProgress = tag.getInt("compilationProgress");
        compilationTime = tag.getInt("compilationTime");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.arcanecodex.dimension_compiler");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.astrolabs.arcanecodex.client.gui.DimensionCompilerMenu(id, inventory, this);
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
    }

    public void dropContents() {
        // No items to drop for now
    }
}