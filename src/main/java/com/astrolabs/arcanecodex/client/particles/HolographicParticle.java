package com.astrolabs.arcanecodex.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HolographicParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    private final float rotSpeed;
    
    protected HolographicParticle(ClientLevel level, double x, double y, double z, 
                                double xSpeed, double ySpeed, double zSpeed,
                                SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 40 + random.nextInt(20);
        this.hasPhysics = false;
        this.friction = 1.0f;
        this.quadSize = 0.1f + random.nextFloat() * 0.05f;
        this.rotSpeed = (random.nextFloat() - 0.5f) * 0.1f;
        
        // Holographic cyan-white color
        float colorVar = 0.7f + random.nextFloat() * 0.3f;
        this.rCol = colorVar * 0.5f;
        this.gCol = colorVar;
        this.bCol = colorVar;
        
        // Slow floating movement
        this.xd = xSpeed * 0.1;
        this.yd = ySpeed * 0.1 + 0.01;
        this.zd = zSpeed * 0.1;
        
        this.alpha = 0.8f;
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Holographic flicker
        this.alpha = 0.8f + 0.2f * Mth.sin(this.age * 0.5f);
        
        // Rotate
        this.oRoll = this.roll;
        this.roll += this.rotSpeed;
        
        // Float upward slowly
        this.yd += 0.001;
        
        // Digital distortion
        if (random.nextFloat() < 0.05f) {
            this.x += (random.nextDouble() - 0.5) * 0.02;
            this.z += (random.nextDouble() - 0.5) * 0.02;
        }
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
    
    @Override
    protected int getLightColor(float partialTick) {
        return 0xF000F0; // Full bright
    }
    
    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        
        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }
        
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                     double x, double y, double z,
                                     double xSpeed, double ySpeed, double zSpeed) {
            return new HolographicParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}