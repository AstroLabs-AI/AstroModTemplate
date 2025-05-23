package com.astrolabs.arcanecodex.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NeuralSparkParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    
    protected NeuralSparkParticle(ClientLevel level, double x, double y, double z, 
                                double xSpeed, double ySpeed, double zSpeed,
                                SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        this.lifetime = 10 + random.nextInt(10);
        this.hasPhysics = false;
        this.friction = 0.9f;
        this.quadSize = 0.03f + random.nextFloat() * 0.02f;
        
        // Neural purple color
        this.rCol = 0.6f;
        this.gCol = 0.2f;
        this.bCol = 0.8f;
        
        // Electric movement
        this.xd = xSpeed + (random.nextDouble() - 0.5) * 0.1;
        this.yd = ySpeed + random.nextDouble() * 0.1;
        this.zd = zSpeed + (random.nextDouble() - 0.5) * 0.1;
        
        this.setSpriteFromAge(sprites);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Erratic neural movement
        if (random.nextFloat() < 0.3f) {
            this.xd = (random.nextDouble() - 0.5) * 0.2;
            this.zd = (random.nextDouble() - 0.5) * 0.2;
        }
        
        // Fade and shrink
        this.alpha = (float)(this.lifetime - this.age) / this.lifetime;
        this.quadSize *= 0.95f;
        
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
            return new NeuralSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
        }
    }
}