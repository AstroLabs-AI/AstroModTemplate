package com.astrolabs.arcanecodex.common.dimensions.features;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class ProbabilityFluxFeature extends Feature<NoneFeatureConfiguration> {
    private static final List<BlockState> PROBABILITY_STATES = List.of(
        Blocks.AMETHYST_BLOCK.defaultBlockState(),
        Blocks.PURPLE_STAINED_GLASS.defaultBlockState(),
        Blocks.MAGENTA_STAINED_GLASS.defaultBlockState(),
        Blocks.PINK_STAINED_GLASS.defaultBlockState(),
        Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
        Blocks.PURPUR_BLOCK.defaultBlockState(),
        Blocks.END_STONE.defaultBlockState(),
        Blocks.CRYING_OBSIDIAN.defaultBlockState(),
        Blocks.SEA_LANTERN.defaultBlockState(),
        Blocks.GLOWSTONE.defaultBlockState()
    );
    
    public ProbabilityFluxFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();
        
        // Create a probability field
        int radius = 8 + random.nextInt(8);
        int placed = 0;
        
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius / 2; y <= radius / 2; y++) {
                for (int z = -radius; z <= radius; z++) {
                    double distance = Math.sqrt(x * x + y * y * 4 + z * z);
                    if (distance <= radius) {
                        BlockPos pos = origin.offset(x, y, z);
                        
                        // Check if we can place here
                        if (level.isEmptyBlock(pos) || level.getBlockState(pos).is(Blocks.END_STONE)) {
                            // Probability of placement decreases with distance
                            double probability = 1.0 - (distance / radius);
                            if (random.nextDouble() < probability * 0.3) {
                                // Select a random probability state
                                BlockState state = PROBABILITY_STATES.get(random.nextInt(PROBABILITY_STATES.size()));
                                level.setBlock(pos, state, 2);
                                placed++;
                            }
                        }
                    }
                }
            }
        }
        
        // Create probability crystals
        if (random.nextInt(3) == 0) {
            createProbabilityCrystal(level, origin.above(radius / 2), random);
        }
        
        return placed > 0;
    }
    
    private void createProbabilityCrystal(WorldGenLevel level, BlockPos pos, RandomSource random) {
        // Central spire
        int height = 5 + random.nextInt(10);
        for (int y = 0; y < height; y++) {
            BlockPos crystalPos = pos.above(y);
            if (level.isEmptyBlock(crystalPos)) {
                level.setBlock(crystalPos, Blocks.AMETHYST_BLOCK.defaultBlockState(), 2);
            }
        }
        
        // Floating fragments around the spire
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 * i) / 8;
            int radius = 3 + random.nextInt(3);
            int x = (int)(Math.cos(angle) * radius);
            int z = (int)(Math.sin(angle) * radius);
            int y = random.nextInt(height);
            
            BlockPos fragmentPos = pos.offset(x, y, z);
            if (level.isEmptyBlock(fragmentPos)) {
                BlockState state = random.nextBoolean() ? 
                    Blocks.PURPLE_STAINED_GLASS.defaultBlockState() : 
                    Blocks.AMETHYST_CLUSTER.defaultBlockState();
                level.setBlock(fragmentPos, state, 2);
            }
        }
    }
}