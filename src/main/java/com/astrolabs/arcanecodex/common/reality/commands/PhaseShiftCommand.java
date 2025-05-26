package com.astrolabs.arcanecodex.common.reality.commands;

import com.astrolabs.arcanecodex.common.reality.RPLParser;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;

public class PhaseShiftCommand extends RPLParser.RPLCommand {
    
    private final String augmentType;
    private final double density;
    private final int duration;
    
    public PhaseShiftCommand(Map<String, String> parameters) throws RPLParser.RPLException {
        super(parameters);
        this.augmentType = getRequiredParam("arg0"); // First argument after player.augment
        this.density = getDoubleParam("density", 0.1);
        this.duration = getIntParam("duration", 200);
    }
    
    public static RPLParser.RPLCommand parse(Map<String, String> parameters) throws RPLParser.RPLException {
        return new PhaseShiftCommand(parameters);
    }
    
    @Override
    public void execute(Level level, Player player, BlockPos pos) throws RPLParser.RPLException {
        if (!augmentType.equals("phase_shift")) {
            throw new RPLParser.RPLException("Unknown augment type: " + augmentType);
        }
        
        // Apply phase shift effect
        applyPhaseShift(player, density, duration);
        
        // Visual feedback
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.HOLOGRAPHIC.get(),
                player.getX(), player.getY() + 1, player.getZ(),
                20, 0.3, 0.5, 0.3, 0.01
            );
        }
    }
    
    private void applyPhaseShift(Player player, double density, int duration) {
        // Low density = more intangible
        if (density < 0.5) {
            // Phase through walls effect
            player.noPhysics = true;
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, duration, 0, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 2, false, false));
            
            // Schedule re-solidification
            player.level().scheduleTick(player.blockPosition(), player.level().getBlockState(player.blockPosition()).getBlock(), duration);
        } else {
            // High density = more solid/resistant
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 4, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, duration, 0, false, false));
        }
    }
    
    @Override
    public int getEnergyCost() {
        return (int)(200 * (1.0 - density)); // More intangible = more energy
    }
}