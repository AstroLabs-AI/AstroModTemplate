package com.astrolabs.arcanecodex.common.blockentities;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.blocks.machines.RealityCompilerBlock;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.capabilities.QuantumEnergyStorage;
import com.astrolabs.arcanecodex.common.reality.RPLParser;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RealityCompilerBlockEntity extends BlockEntity implements MenuProvider {
    
    private final QuantumEnergyStorage energyStorage = new QuantumEnergyStorage(50000);
    private final LazyOptional<IQuantumEnergy> energyOptional = LazyOptional.of(() -> energyStorage);
    
    private ItemStack codeBook = ItemStack.EMPTY;
    private List<String> codeLines = new ArrayList<>();
    private int currentLine = 0;
    private boolean executing = false;
    private int executionCooldown = 0;
    private UUID lastUser = null;
    
    public RealityCompilerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REALITY_COMPILER.get(), pos, state);
    }
    
    public static void serverTick(Level level, BlockPos pos, BlockState state, RealityCompilerBlockEntity blockEntity) {
        if (blockEntity.executionCooldown > 0) {
            blockEntity.executionCooldown--;
        }
        
        if (blockEntity.executing && blockEntity.executionCooldown == 0) {
            blockEntity.executeNextLine(level, pos);
        }
        
        // Update block state
        boolean shouldBeExecuting = blockEntity.executing;
        if (state.getValue(RealityCompilerBlock.EXECUTING) != shouldBeExecuting) {
            level.setBlock(pos, state.setValue(RealityCompilerBlock.EXECUTING, shouldBeExecuting), 3);
        }
    }
    
    public static void clientTick(Level level, BlockPos pos, BlockState state, RealityCompilerBlockEntity blockEntity) {
        if (state.getValue(RealityCompilerBlock.EXECUTING)) {
            // Spawn holographic particles during execution
            if (level.random.nextFloat() < 0.3f) {
                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                double y = pos.getY() + 1.5;
                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                
                level.addParticle(
                    com.astrolabs.arcanecodex.common.particles.ModParticles.HOLOGRAPHIC.get(),
                    x, y, z, 0, 0.05, 0
                );
            }
        }
    }
    
    public void startExecution(Player player) {
        if (!executing && !codeLines.isEmpty()) {
            executing = true;
            currentLine = 0;
            lastUser = player.getUUID();
            setChanged();
        }
    }
    
    private void executeNextLine(Level level, BlockPos pos) {
        if (currentLine >= codeLines.size()) {
            // Execution complete
            executing = false;
            currentLine = 0;
            setChanged();
            return;
        }
        
        String code = codeLines.get(currentLine);
        currentLine++;
        
        // Skip empty lines and comments
        if (code.trim().isEmpty() || code.trim().startsWith("//")) {
            return;
        }
        
        try {
            // Parse and execute RPL command
            RPLParser.RPLCommand command = RPLParser.parse(code);
            
            // Check energy cost
            int energyCost = command.getEnergyCost();
            long availableEnergy = energyStorage.getEnergyStored(IQuantumEnergy.EnergyType.QUANTUM_FOAM);
            
            if (availableEnergy >= energyCost) {
                // Find executing player
                Player player = null;
                if (lastUser != null) {
                    player = level.getServer().getPlayerList().getPlayer(lastUser);
                }
                
                if (player != null) {
                    // Execute command
                    command.execute(level, player, pos);
                    
                    // Consume energy
                    energyStorage.extractEnergy(IQuantumEnergy.EnergyType.QUANTUM_FOAM, energyCost, false);
                    
                    // Feedback
                    player.sendSystemMessage(Component.literal("Executed: " + code).withStyle(style -> style.withColor(0x00FFFF)));
                }
            } else {
                // Not enough energy
                if (lastUser != null) {
                    Player player = level.getServer().getPlayerList().getPlayer(lastUser);
                    if (player != null) {
                        player.sendSystemMessage(Component.literal("Insufficient energy! Need " + energyCost).withStyle(style -> style.withColor(0xFF0000)));
                    }
                }
                executing = false;
            }
        } catch (RPLParser.RPLException e) {
            // Syntax error
            if (lastUser != null) {
                Player player = level.getServer().getPlayerList().getPlayer(lastUser);
                if (player != null) {
                    player.sendSystemMessage(Component.literal("RPL Error: " + e.getMessage()).withStyle(style -> style.withColor(0xFF0000)));
                }
            }
            executing = false;
        }
        
        // Cooldown between commands
        executionCooldown = 20; // 1 second
    }
    
    public void setCodeBook(ItemStack book) {
        this.codeBook = book.copy();
        
        // Extract code from book
        codeLines.clear();
        if (book.hasTag() && book.getTag().contains("pages")) {
            ListTag pages = book.getTag().getList("pages", 8);
            for (int i = 0; i < pages.size(); i++) {
                String pageText = pages.getString(i);
                // Parse JSON text component
                try {
                    Component component = Component.Serializer.fromJson(pageText);
                    if (component != null) {
                        String plainText = component.getString();
                        String[] lines = plainText.split("\n");
                        for (String line : lines) {
                            if (!line.trim().isEmpty()) {
                                codeLines.add(line.trim());
                            }
                        }
                    }
                } catch (Exception e) {
                    // If JSON parsing fails, try plain text
                    codeLines.add(pageText);
                }
            }
        }
        
        setChanged();
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.deserializeNBT(tag.getCompound("Energy"));
        codeBook = ItemStack.of(tag.getCompound("CodeBook"));
        
        codeLines.clear();
        ListTag codeList = tag.getList("CodeLines", 8);
        for (int i = 0; i < codeList.size(); i++) {
            codeLines.add(codeList.getString(i));
        }
        
        currentLine = tag.getInt("CurrentLine");
        executing = tag.getBoolean("Executing");
        executionCooldown = tag.getInt("ExecutionCooldown");
        
        if (tag.hasUUID("LastUser")) {
            lastUser = tag.getUUID("LastUser");
        }
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Energy", energyStorage.serializeNBT());
        tag.put("CodeBook", codeBook.save(new CompoundTag()));
        
        ListTag codeList = new ListTag();
        for (String line : codeLines) {
            codeList.add(net.minecraft.nbt.StringTag.valueOf(line));
        }
        tag.put("CodeLines", codeList);
        
        tag.putInt("CurrentLine", currentLine);
        tag.putBoolean("Executing", executing);
        tag.putInt("ExecutionCooldown", executionCooldown);
        
        if (lastUser != null) {
            tag.putUUID("LastUser", lastUser);
        }
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
        return Component.translatable("block.arcanecodex.reality_compiler");
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new com.astrolabs.arcanecodex.client.gui.RealityCompilerMenu(id, inventory, worldPosition);
    }
    
    public List<String> getCodeLines() {
        return codeLines;
    }
    
    public boolean isExecuting() {
        return executing;
    }
}