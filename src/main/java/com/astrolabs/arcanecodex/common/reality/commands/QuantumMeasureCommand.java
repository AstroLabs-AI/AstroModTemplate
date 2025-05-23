package com.astrolabs.arcanecodex.common.reality.commands;

import com.astrolabs.arcanecodex.common.reality.RPLParser;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Map;

public class QuantumMeasureCommand extends RPLParser.RPLCommand {
    
    private final String measurement;
    private final int radius;
    
    public QuantumMeasureCommand(Map<String, String> parameters) {
        super(parameters);
        this.measurement = getParam("arg0", "entities");
        this.radius = getIntParam("radius", 10);
    }
    
    public static RPLParser.RPLCommand parse(Map<String, String> parameters) throws RPLParser.RPLException {
        return new QuantumMeasureCommand(parameters);
    }
    
    @Override
    public void execute(Level level, Player player, BlockPos pos) {
        switch (measurement) {
            case "entities" -> measureEntities(level, player, pos);
            case "energy" -> measureEnergy(level, player, pos);
            case "probability" -> measureProbability(level, player, pos);
            default -> player.sendSystemMessage(Component.literal("Unknown measurement: " + measurement));
        }
    }
    
    private void measureEntities(Level level, Player player, BlockPos pos) {
        AABB area = new AABB(pos).inflate(radius);
        List<Entity> entities = level.getEntities(player, area);
        
        player.sendSystemMessage(Component.literal("=== Quantum Entity Scan ==="));
        player.sendSystemMessage(Component.literal("Detected " + entities.size() + " entities in " + radius + " block radius"));
        
        // Collapse quantum state - randomly affect some entities
        for (Entity entity : entities) {
            if (level.random.nextFloat() < 0.1f) { // 10% chance
                // Quantum observation affects the entity
                entity.setGlowingTag(!entity.isCurrentlyGlowing());
                level.sendParticles(
                    com.astrolabs.arcanecodex.common.particles.ModParticles.QUANTUM_ENERGY.get(),
                    entity.getX(), entity.getY() + entity.getBbHeight()/2, entity.getZ(),
                    5, 0.2, 0.2, 0.2, 0.05
                );
            }
        }
    }
    
    private void measureEnergy(Level level, Player player, BlockPos pos) {
        // TODO: Measure quantum energy in area
        player.sendSystemMessage(Component.literal("Quantum energy measurement not yet implemented"));
    }
    
    private void measureProbability(Level level, Player player, BlockPos pos) {
        // Generate quantum probability field
        float probability = level.random.nextFloat();
        player.sendSystemMessage(Component.literal("Local probability coefficient: " + String.format("%.3f", probability)));
        
        if (probability > 0.8f) {
            player.sendSystemMessage(Component.literal("High probability detected - reality is stable"));
        } else if (probability < 0.2f) {
            player.sendSystemMessage(Component.literal("Low probability detected - reality glitches possible!"));
            // Spawn glitch particles
            level.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.REALITY_GLITCH.get(),
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                20, 1, 1, 1, 0.1
            );
        }
    }
    
    @Override
    public int getEnergyCost() {
        return 50 + (radius * 5);
    }
}