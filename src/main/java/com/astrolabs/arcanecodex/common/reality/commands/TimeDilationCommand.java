package com.astrolabs.arcanecodex.common.reality.commands;

import com.astrolabs.arcanecodex.common.reality.RPLParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;

public class TimeDilationCommand extends RPLParser.RPLCommand {
    
    private final double factor;
    private final int radius;
    private final int duration;
    
    public TimeDilationCommand(Map<String, String> parameters) {
        super(parameters);
        this.factor = getDoubleParam("factor", 2.0);
        this.radius = getIntParam("radius", 5);
        this.duration = getIntParam("duration", 200);
    }
    
    public static RPLParser.RPLCommand parse(Map<String, String> parameters) throws RPLParser.RPLException {
        return new TimeDilationCommand(parameters);
    }
    
    @Override
    public void execute(Level level, Player player, BlockPos pos) {
        // TODO: Implement time dilation field
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
            "Time dilation field created: " + factor + "x speed for " + duration + " ticks"
        ));
        
        // Visual effect
        for (int i = 0; i < 10; i++) {
            double angle = (Math.PI * 2) * i / 10;
            double x = pos.getX() + 0.5 + Math.cos(angle) * radius;
            double z = pos.getZ() + 0.5 + Math.sin(angle) * radius;
            
            level.sendParticles(
                com.astrolabs.arcanecodex.common.particles.ModParticles.QUANTUM_ENERGY.get(),
                x, pos.getY() + 1, z,
                1, 0, 0.5, 0, 0.01
            );
        }
    }
    
    @Override
    public int getEnergyCost() {
        return (int)(factor * radius * duration / 10);
    }
}