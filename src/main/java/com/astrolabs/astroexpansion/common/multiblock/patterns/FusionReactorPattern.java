package com.astrolabs.astroexpansion.common.multiblock.patterns;

import com.astrolabs.astroexpansion.api.multiblock.IMultiblockMatcher;
import com.astrolabs.astroexpansion.api.multiblock.IMultiblockPattern;
import com.astrolabs.astroexpansion.api.multiblock.MultiblockMatcher;
import com.astrolabs.astroexpansion.common.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class FusionReactorPattern implements IMultiblockPattern {
    private static final String[][][] PATTERN = {
        // Layer 0 (bottom)
        {
            {"CCC"},
            {"CCC"},
            {"CCC"}
        },
        // Layer 1 (middle)
        {
            {"CGC"},
            {"G G"},
            {"CGC"}
        },
        // Layer 2 (top)
        {
            {"CCC"},
            {"CCC"},
            {"CCC"}
        }
    };
    
    private final Map<Character, IMultiblockMatcher> matchers = new HashMap<>();
    
    public FusionReactorPattern() {
        matchers.put('C', MultiblockMatcher.block(ModBlocks.FUSION_REACTOR_CASING.get()));
        matchers.put('G', MultiblockMatcher.block(Blocks.GLASS));
        matchers.put(' ', MultiblockMatcher.block(Blocks.AIR));
    }
    
    @Override
    public String getPatternId() {
        return "fusion_reactor";
    }
    
    @Override
    public int getWidth() {
        return 3;
    }
    
    @Override
    public int getHeight() {
        return 3;
    }
    
    @Override
    public int getDepth() {
        return 3;
    }
    
    @Override
    public BlockPos getMasterOffset() {
        return new BlockPos(1, 1, 1); // Center of the middle layer
    }
    
    @Override
    public Map<Character, IMultiblockMatcher> getMatchers() {
        return matchers;
    }
    
    @Override
    public String[][][] getPattern() {
        return PATTERN;
    }
}