package com.astrolabs.arcanecodex.common.systems.reality;

import com.astrolabs.arcanecodex.common.particles.ModParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class RealityGlitchManager {
    
    private static final Map<Level, List<RealityGlitch>> ACTIVE_GLITCHES = new WeakHashMap<>();
    
    public static class RealityGlitch {
        public final BlockPos center;
        public final GlitchType type;
        public final int duration;
        public final float intensity;
        private int ticksExisted = 0;
        
        public RealityGlitch(BlockPos center, GlitchType type, int duration, float intensity) {
            this.center = center;
            this.type = type;
            this.duration = duration;
            this.intensity = intensity;
        }
        
        public boolean tick(ServerLevel level) {
            ticksExisted++;
            
            if (ticksExisted > duration) {
                return false; // Remove glitch
            }
            
            // Apply glitch effects
            type.applyEffect(level, center, intensity, ticksExisted);
            
            // Spawn particles
            if (ticksExisted % 5 == 0) {
                spawnGlitchParticles(level);
            }
            
            return true;
        }
        
        private void spawnGlitchParticles(ServerLevel level) {
            RandomSource random = level.random;
            int particleCount = (int)(5 * intensity);
            
            for (int i = 0; i < particleCount; i++) {
                double x = center.getX() + 0.5 + (random.nextDouble() - 0.5) * 6;
                double y = center.getY() + 0.5 + (random.nextDouble() - 0.5) * 4;
                double z = center.getZ() + 0.5 + (random.nextDouble() - 0.5) * 6;
                
                level.sendParticles(
                    ModParticles.REALITY_GLITCH.get(),
                    x, y, z,
                    1,
                    0.1, 0.1, 0.1,
                    0.02
                );
            }
        }
    }
    
    public enum GlitchType {
        GRAVITY_INVERSION {
            @Override
            public void applyEffect(ServerLevel level, BlockPos center, float intensity, int ticks) {
                AABB area = new AABB(center).inflate(5 * intensity);
                List<Entity> entities = level.getEntitiesOfClass(Entity.class, area);
                
                for (Entity entity : entities) {
                    if (entity instanceof ItemEntity || entity instanceof LivingEntity) {
                        Vec3 motion = entity.getDeltaMovement();
                        entity.setDeltaMovement(motion.x, Math.min(0.5, motion.y + 0.05 * intensity), motion.z);
                        
                        if (entity instanceof LivingEntity living && ticks % 20 == 0) {
                            living.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, (int)intensity));
                        }
                    }
                }
            }
        },
        
        TEMPORAL_STUTTER {
            @Override
            public void applyEffect(ServerLevel level, BlockPos center, float intensity, int ticks) {
                if (ticks % 20 == 0) { // Every second
                    AABB area = new AABB(center).inflate(8 * intensity);
                    List<Entity> entities = level.getEntitiesOfClass(Entity.class, area);
                    
                    for (Entity entity : entities) {
                        // Randomly teleport entities back a short distance
                        if (level.random.nextFloat() < intensity * 0.3f) {
                            Vec3 pos = entity.position();
                            Vec3 motion = entity.getDeltaMovement();
                            entity.teleportTo(
                                pos.x - motion.x * 20,
                                pos.y - motion.y * 20,
                                pos.z - motion.z * 20
                            );
                            
                            // Visual effect
                            level.sendParticles(
                                ParticleTypes.PORTAL,
                                entity.getX(), entity.getY() + 1, entity.getZ(),
                                10, 0.5, 0.5, 0.5, 0.1
                            );
                        }
                    }
                }
            }
        },
        
        BLOCK_PHASING {
            @Override
            public void applyEffect(ServerLevel level, BlockPos center, float intensity, int ticks) {
                if (ticks % 10 == 0) {
                    RandomSource random = level.random;
                    int radius = (int)(3 * intensity);
                    
                    // Make random blocks temporarily passable
                    for (int i = 0; i < intensity * 2; i++) {
                        BlockPos pos = center.offset(
                            random.nextInt(radius * 2 + 1) - radius,
                            random.nextInt(radius * 2 + 1) - radius,
                            random.nextInt(radius * 2 + 1) - radius
                        );
                        
                        BlockState state = level.getBlockState(pos);
                        if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
                            // Store original block and make it temporarily air
                            // In a real implementation, we'd need to store and restore these
                            level.sendParticles(
                                ModParticles.HOLOGRAPHIC.get(),
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                20, 0.5, 0.5, 0.5, 0.01
                            );
                        }
                    }
                }
            }
        },
        
        DIMENSIONAL_TEAR {
            @Override
            public void applyEffect(ServerLevel level, BlockPos center, float intensity, int ticks) {
                if (ticks % 40 == 0) {
                    // Create a small area of instability
                    AABB area = new AABB(center).inflate(4 * intensity);
                    List<Entity> entities = level.getEntitiesOfClass(Entity.class, area);
                    
                    for (Entity entity : entities) {
                        // Apply random effects
                        if (level.random.nextFloat() < intensity * 0.2f) {
                            if (entity instanceof LivingEntity living) {
                                int effect = level.random.nextInt(4);
                                switch (effect) {
                                    case 0 -> living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
                                    case 1 -> living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));
                                    case 2 -> living.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 80, 0));
                                    case 3 -> {
                                        // Teleport to random nearby location
                                        double x = entity.getX() + (level.random.nextDouble() - 0.5) * 10;
                                        double z = entity.getZ() + (level.random.nextDouble() - 0.5) * 10;
                                        entity.teleportTo(x, entity.getY(), z);
                                    }
                                }
                            }
                        }
                    }
                    
                    // Sound effect
                    level.playSound(null, center, SoundEvents.ENDERMAN_TELEPORT, 
                        SoundSource.AMBIENT, 0.5f * intensity, 0.5f);
                }
            }
        };
        
        public abstract void applyEffect(ServerLevel level, BlockPos center, float intensity, int ticks);
    }
    
    public static void createGlitch(ServerLevel level, BlockPos pos, GlitchType type, int duration, float intensity) {
        RealityGlitch glitch = new RealityGlitch(pos, type, duration, intensity);
        
        List<RealityGlitch> glitches = ACTIVE_GLITCHES.computeIfAbsent(level, k -> new ArrayList<>());
        glitches.add(glitch);
        
        // Initial effect
        level.playSound(null, pos, SoundEvents.CHORUS_FRUIT_TELEPORT, 
            SoundSource.AMBIENT, 1.0f, 0.5f);
        
        // Spawn initial burst of particles
        for (int i = 0; i < 50; i++) {
            double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 3;
            double y = pos.getY() + 0.5 + (level.random.nextDouble() - 0.5) * 3;
            double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 3;
            
            level.sendParticles(
                ModParticles.REALITY_GLITCH.get(),
                x, y, z,
                1,
                0, 0, 0,
                0.1
            );
        }
    }
    
    public static void tickGlitches(ServerLevel level) {
        List<RealityGlitch> glitches = ACTIVE_GLITCHES.get(level);
        if (glitches != null) {
            glitches.removeIf(glitch -> !glitch.tick(level));
        }
    }
    
    public static void createRandomGlitch(ServerLevel level, BlockPos center, RandomSource random) {
        GlitchType[] types = GlitchType.values();
        GlitchType type = types[random.nextInt(types.length)];
        
        int duration = 100 + random.nextInt(300); // 5-20 seconds
        float intensity = 0.5f + random.nextFloat() * 1.5f; // 0.5-2.0
        
        createGlitch(level, center, type, duration, intensity);
    }
}