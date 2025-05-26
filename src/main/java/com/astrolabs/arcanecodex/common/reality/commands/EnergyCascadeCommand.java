package com.astrolabs.arcanecodex.common.reality.commands;

import com.astrolabs.arcanecodex.api.IQuantumEnergy;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.reality.RPLParser;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import java.util.Map;

public class EnergyCascadeCommand extends RPLParser.RPLCommand {
    
    private final IQuantumEnergy.EnergyType energyType;
    private final int amount;
    private final int radius;
    
    public EnergyCascadeCommand(Map<String, String> parameters) throws RPLParser.RPLException {
        super(parameters);
        
        String typeStr = getParam("type", "quantum_foam");
        this.energyType = parseEnergyType(typeStr);
        this.amount = getIntParam("amount", 1000);
        this.radius = getIntParam("radius", 5);
    }
    
    private IQuantumEnergy.EnergyType parseEnergyType(String type) throws RPLParser.RPLException {
        return switch (type.toLowerCase()) {
            case "coherent_light" -> IQuantumEnergy.EnergyType.COHERENT_LIGHT;
            case "quantum_foam" -> IQuantumEnergy.EnergyType.QUANTUM_FOAM;
            case "neural_charge" -> IQuantumEnergy.EnergyType.NEURAL_CHARGE;
            case "temporal_flux" -> IQuantumEnergy.EnergyType.TEMPORAL_FLUX;
            case "dark_current" -> IQuantumEnergy.EnergyType.DARK_CURRENT;
            case "synthesis_wave" -> IQuantumEnergy.EnergyType.SYNTHESIS_WAVE;
            default -> throw new RPLParser.RPLException("Unknown energy type: " + type);
        };
    }
    
    public static RPLParser.RPLCommand parse(Map<String, String> parameters) throws RPLParser.RPLException {
        return new EnergyCascadeCommand(parameters);
    }
    
    @Override
    public void execute(Level level, Player player, BlockPos pos) {
        // Find all quantum energy storages in radius
        AABB area = new AABB(pos).inflate(radius);
        BlockPos.betweenClosed(
            new BlockPos((int)area.minX, (int)area.minY, (int)area.minZ),
            new BlockPos((int)area.maxX, (int)area.maxY, (int)area.maxZ)
        ).forEach(checkPos -> {
            BlockEntity be = level.getBlockEntity(checkPos);
            if (be != null) {
                be.getCapability(ModCapabilities.QUANTUM_ENERGY).ifPresent(energy -> {
                    // Trigger cascade effect
                    long inserted = energy.insertEnergy(energyType, amount, false);
                    if (inserted > 0) {
                        // Visual cascade effect
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(
                                com.astrolabs.arcanecodex.common.particles.ModParticles.QUANTUM_ENERGY.get(),
                                checkPos.getX() + 0.5, checkPos.getY() + 0.5, checkPos.getZ() + 0.5,
                                10, 0.3, 0.3, 0.3, 0.1
                            );
                        }
                        
                        // Chain reaction - spread to nearby blocks
                        if (level.random.nextFloat() < 0.3f) {
                            for (BlockPos neighbor : BlockPos.betweenClosed(
                                checkPos.offset(-1, -1, -1), 
                                checkPos.offset(1, 1, 1)
                            )) {
                                if (!neighbor.equals(checkPos)) {
                                    BlockEntity neighborBe = level.getBlockEntity(neighbor);
                                    if (neighborBe != null) {
                                        neighborBe.getCapability(ModCapabilities.QUANTUM_ENERGY).ifPresent(neighborEnergy -> {
                                            neighborEnergy.insertEnergy(energyType, amount / 2, false);
                                        });
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
    }
    
    @Override
    public int getEnergyCost() {
        return amount / 10; // 10% of cascaded energy
    }
}