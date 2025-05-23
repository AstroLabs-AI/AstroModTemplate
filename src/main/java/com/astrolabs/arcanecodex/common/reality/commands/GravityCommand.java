package com.astrolabs.arcanecodex.common.reality.commands;

import com.astrolabs.arcanecodex.common.reality.RPLParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class GravityCommand extends RPLParser.RPLCommand {
    
    private final String type;
    private final double strength;
    private final int duration;
    private final BlockPos targetPos;
    
    public GravityCommand(Map<String, String> parameters) throws RPLParser.RPLException {
        super(parameters);
        this.type = getRequiredParam("type");
        this.strength = getDoubleParam("strength", 9.8);
        this.duration = getIntParam("duration", 100);
        
        // Parse position - for now, use player's looking position
        String posString = getParam("position", "player.lookingAt()");
        this.targetPos = null; // Will be calculated at execution
    }
    
    public static RPLParser.RPLCommand parse(Map<String, String> parameters) throws RPLParser.RPLException {
        return new GravityCommand(parameters);
    }
    
    @Override
    public void execute(Level level, Player player, BlockPos pos) throws RPLParser.RPLException {
        if (!type.equals("gravitational_anomaly")) {
            throw new RPLParser.RPLException("Unknown gravity type: " + type);
        }
        
        // Calculate target position
        BlockPos effectPos = targetPos;
        if (effectPos == null) {
            // Get block player is looking at
            Vec3 lookVec = player.getLookAngle();
            Vec3 eyePos = player.getEyePosition();
            Vec3 targetVec = eyePos.add(lookVec.scale(10));
            effectPos = new BlockPos((int)targetVec.x, (int)targetVec.y, (int)targetVec.z);
        }
        
        // Create gravity well effect
        createGravityWell(level, effectPos, strength, duration);
    }
    
    private void createGravityWell(Level level, BlockPos center, double strength, int duration) {
        // Get all entities in range
        double range = 10.0;
        AABB area = new AABB(center).inflate(range);
        List<Entity> entities = level.getEntities(null, area);
        
        for (Entity entity : entities) {
            if (entity instanceof Player && ((Player)entity).isCreative()) {
                continue; // Don't affect creative players
            }
            
            // Calculate pull vector
            Vec3 entityPos = entity.position();
            Vec3 centerPos = Vec3.atCenterOf(center);
            Vec3 pullVector = centerPos.subtract(entityPos).normalize();
            
            // Apply force based on distance
            double distance = entityPos.distanceTo(centerPos);
            if (distance > 0.5 && distance < range) {
                double force = strength / (distance * distance); // Inverse square law
                entity.setDeltaMovement(entity.getDeltaMovement().add(pullVector.scale(force * 0.1)));
            }
        }
        
        // Spawn visual effects
        if (level.random.nextFloat() < 0.3f) {
            level.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.REALITY_GLITCH.get(),
                center.getX() + 0.5, center.getY() + 0.5, center.getZ() + 0.5,
                3, 0.5, 0.5, 0.5, 0.02
            );
        }
    }
    
    @Override
    public int getEnergyCost() {
        return (int)(strength * duration / 10);
    }
}