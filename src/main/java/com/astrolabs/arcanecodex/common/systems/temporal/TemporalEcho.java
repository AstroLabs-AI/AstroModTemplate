package com.astrolabs.arcanecodex.common.systems.temporal;

import com.astrolabs.arcanecodex.common.particles.ModParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class TemporalEcho {
    
    private final EntityType<?> entityType;
    private final Vec3 position;
    private final float yaw;
    private final float pitch;
    private final Vec3 motion;
    private final float health;
    private final long creationTime;
    private int lifetime = 200; // 10 seconds
    
    private UUID echoEntityId = null;
    
    public TemporalEcho(LivingEntity entity, long gameTime) {
        this.entityType = entity.getType();
        this.position = entity.position();
        this.yaw = entity.getYRot();
        this.pitch = entity.getXRot();
        this.motion = entity.getDeltaMovement();
        this.health = entity.getHealth();
        this.creationTime = gameTime;
    }
    
    private TemporalEcho(CompoundTag tag) {
        String typeId = tag.getString("EntityType");
        this.entityType = EntityType.byString(typeId).orElse(EntityType.ZOMBIE);
        
        this.position = new Vec3(
            tag.getDouble("X"),
            tag.getDouble("Y"),
            tag.getDouble("Z")
        );
        this.yaw = tag.getFloat("Yaw");
        this.pitch = tag.getFloat("Pitch");
        this.motion = new Vec3(
            tag.getDouble("MotionX"),
            tag.getDouble("MotionY"),
            tag.getDouble("MotionZ")
        );
        this.health = tag.getFloat("Health");
        this.creationTime = tag.getLong("CreationTime");
        this.lifetime = tag.getInt("Lifetime");
        
        if (tag.hasUUID("EchoEntityId")) {
            this.echoEntityId = tag.getUUID("EchoEntityId");
        }
    }
    
    public boolean tick(Level level) {
        lifetime--;
        
        if (lifetime <= 0) {
            // Remove echo entity if it exists
            if (echoEntityId != null && level instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(echoEntityId);
                if (entity != null) {
                    // Fade out effect
                    serverLevel.sendParticles(
                        ModParticles.HOLOGRAPHIC.get(),
                        entity.getX(), entity.getY() + 1, entity.getZ(),
                        20, 0.3, 0.5, 0.3, 0.05
                    );
                    entity.discard();
                }
            }
            return false;
        }
        
        // Spawn echo entity on first tick
        if (echoEntityId == null && level instanceof ServerLevel serverLevel) {
            spawnEchoEntity(serverLevel);
        }
        
        // Update echo visual
        if (echoEntityId != null && level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(echoEntityId);
            if (entity != null) {
                // Make it ghostly
                entity.setInvisible(lifetime % 4 < 2); // Flicker effect
                
                // Spawn particles
                if (lifetime % 5 == 0) {
                    serverLevel.sendParticles(
                        ModParticles.HOLOGRAPHIC.get(),
                        entity.getX(), entity.getY() + 1, entity.getZ(),
                        5, 0.2, 0.5, 0.2, 0.01
                    );
                }
            }
        }
        
        return true;
    }
    
    private void spawnEchoEntity(ServerLevel level) {
        Optional<Entity> optEntity = EntityType.create(entityType, level);
        if (optEntity.isPresent()) {
            Entity entity = optEntity.get();
            
            entity.moveTo(position.x, position.y, position.z, yaw, pitch);
            entity.setDeltaMovement(motion);
            entity.setGlowingTag(true);
            entity.setInvulnerable(true);
            entity.setSilent(true);
            
            if (entity instanceof LivingEntity living) {
                living.setHealth(health);
                
                // Make it non-aggressive
                if (living instanceof Mob mob) {
                    mob.setNoAi(true);
                }
                
                // Make it translucent (this is a simplified approach)
                living.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0);
            }
            
            level.addFreshEntity(entity);
            echoEntityId = entity.getUUID();
        }
    }
    
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        
        tag.putString("EntityType", EntityType.getKey(entityType).toString());
        tag.putDouble("X", position.x);
        tag.putDouble("Y", position.y);
        tag.putDouble("Z", position.z);
        tag.putFloat("Yaw", yaw);
        tag.putFloat("Pitch", pitch);
        tag.putDouble("MotionX", motion.x);
        tag.putDouble("MotionY", motion.y);
        tag.putDouble("MotionZ", motion.z);
        tag.putFloat("Health", health);
        tag.putLong("CreationTime", creationTime);
        tag.putInt("Lifetime", lifetime);
        
        if (echoEntityId != null) {
            tag.putUUID("EchoEntityId", echoEntityId);
        }
        
        return tag;
    }
    
    public static TemporalEcho load(CompoundTag tag) {
        return new TemporalEcho(tag);
    }
}