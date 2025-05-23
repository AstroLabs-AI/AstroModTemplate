package com.astrolabs.arcanecodex.common.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuantumParticle extends TextureSheetParticle {
    
    private final SpriteSet sprites;
    private final float baseRed, baseGreen, baseBlue;
    
    protected QuantumParticle(ClientLevel level, double x, double y, double z, 
                            double xSpeed, double ySpeed, double zSpeed,
                            int color, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        
        // Extract RGB from color
        this.baseRed = ((color >> 16) & 0xFF) / 255.0f;
        this.baseGreen = ((color >> 8) & 0xFF) / 255.0f;
        this.baseBlue = (color & 0xFF) / 255.0f;
        
        this.lifetime = 20 + random.nextInt(20);
        this.hasPhysics = false;
        this.friction = 0.96f;
        this.quadSize = 0.05f + random.nextFloat() * 0.05f;
        
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        
        this.setSpriteFromAge(sprites);
        updateColors();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Quantum behavior - slight random movement
        this.xd += (random.nextDouble() - 0.5) * 0.01;
        this.yd += (random.nextDouble() - 0.5) * 0.01;
        this.zd += (random.nextDouble() - 0.5) * 0.01;
        
        // Fade out
        if (this.age >= this.lifetime - 5) {
            this.alpha = (this.lifetime - this.age) / 5.0f;
        }
        
        updateColors();
        this.setSpriteFromAge(sprites);
    }
    
    private void updateColors() {
        // Quantum fluctuation effect
        float fluctuation = 0.2f * (float)Math.sin(this.age * 0.5);
        this.rCol = Math.min(1.0f, baseRed + fluctuation);
        this.gCol = Math.min(1.0f, baseGreen + fluctuation);
        this.bCol = Math.min(1.0f, baseBlue + fluctuation);
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
            // Random color from quantum energy types
            int[] colors = {0x00FFFF, 0xFF00FF, 0x9932CC, 0xFFD700, 0x4B0082, 0x00FF00};
            int color = colors[level.random.nextInt(colors.length)];
            
            return new QuantumParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, color, sprites);
        }
    }
}