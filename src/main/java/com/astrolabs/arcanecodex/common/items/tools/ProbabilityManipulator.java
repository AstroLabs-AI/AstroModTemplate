package com.astrolabs.arcanecodex.common.items.tools;

import com.astrolabs.arcanecodex.api.IConsciousness;
import com.astrolabs.arcanecodex.common.capabilities.ModCapabilities;
import com.astrolabs.arcanecodex.common.particles.ModParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProbabilityManipulator extends Item {
    
    private static final int CHARGE_TIME = 60; // 3 seconds
    private static final int COOLDOWN = 100; // 5 seconds
    
    public ProbabilityManipulator(Properties properties) {
        super(properties.stacksTo(1).durability(500));
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level level, Player player, int timeLeft) {
        int chargeTime = this.getUseDuration(stack) - timeLeft;
        
        if (chargeTime >= CHARGE_TIME && !level.isClientSide) {
            // Get probability charge level
            float charge = Math.min(1.0f, chargeTime / (float)(CHARGE_TIME * 2));
            
            // Consume neural charge
            player.getCapability(ModCapabilities.CONSCIOUSNESS).ifPresent(consciousness -> {
                int cost = (int)(50 * charge);
                if (consciousness.getNeuralCharge() >= cost) {
                    consciousness.addNeuralCharge(-cost);
                    
                    // Apply probability manipulation
                    applyProbabilityManipulation(level, player, charge);
                    
                    // Damage item
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    
                    // Cooldown
                    player.getCooldowns().addCooldown(this, COOLDOWN);
                } else {
                    player.sendSystemMessage(Component.literal("Insufficient neural charge!")
                        .withStyle(ChatFormatting.RED));
                }
            });
        }
    }
    
    private void applyProbabilityManipulation(Level level, Player player, float charge) {
        RandomSource random = level.random;
        int radius = (int)(10 * charge);
        
        // Visual effect
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50 * charge; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double distance = random.nextDouble() * radius;
                
                double x = player.getX() + Math.cos(angle) * distance;
                double y = player.getY() + 1 + random.nextDouble() * 2;
                double z = player.getZ() + Math.sin(angle) * distance;
                
                serverLevel.sendParticles(
                    ModParticles.QUANTUM_ENERGY.get(),
                    x, y, z,
                    1, 0, 0, 0, 0.05
                );
            }
        }
        
        // Sound effect
        level.playSound(null, player.blockPosition(), SoundEvents.BEACON_POWER_SELECT, 
            SoundSource.PLAYERS, 1.0f, 0.5f + charge * 0.5f);
        
        // Apply random effects based on charge
        int effectCount = random.nextInt(3) + (int)(charge * 3);
        
        for (int i = 0; i < effectCount; i++) {
            ProbabilityEffect effect = ProbabilityEffect.values()[random.nextInt(ProbabilityEffect.values().length)];
            effect.apply(level, player, radius, charge, random);
        }
        
        player.sendSystemMessage(Component.literal("Reality probabilities shifted!")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        
        if (!level.isClientSide && player != null && !player.getCooldowns().isOnCooldown(this)) {
            BlockState state = level.getBlockState(context.getClickedPos());
            
            // Special interaction with certain blocks
            if (state.is(Blocks.STONE)) {
                // Small chance to transmute to ore
                if (level.random.nextFloat() < 0.1f) {
                    Block[] ores = {Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.COPPER_ORE, 
                                   Blocks.GOLD_ORE, Blocks.DIAMOND_ORE};
                    Block newBlock = ores[level.random.nextInt(ores.length)];
                    
                    level.setBlock(context.getClickedPos(), newBlock.defaultBlockState(), 3);
                    
                    // Effects
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(
                            ModParticles.REALITY_GLITCH.get(),
                            context.getClickedPos().getX() + 0.5,
                            context.getClickedPos().getY() + 0.5,
                            context.getClickedPos().getZ() + 0.5,
                            20, 0.5, 0.5, 0.5, 0.05
                        );
                    }
                    
                    player.sendSystemMessage(Component.literal("Probability collapse: Ore manifested!")
                        .withStyle(ChatFormatting.GOLD));
                    
                    context.getItemInHand().hurtAndBreak(5, player, p -> p.broadcastBreakEvent(context.getHand()));
                    player.getCooldowns().addCooldown(this, 200);
                    
                    return InteractionResult.SUCCESS;
                }
            }
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // Can be held indefinitely
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Collapses quantum probability waves")
            .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.literal("Hold to charge, release to manipulate")
            .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Neural Charge Cost: 25-50")
            .withStyle(ChatFormatting.AQUA));
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
    
    private enum ProbabilityEffect {
        LUCKY_DROPS {
            @Override
            void apply(Level level, Player player, int radius, float charge, RandomSource random) {
                // Increase drop rates from entities
                AABB area = player.getBoundingBox().inflate(radius);
                List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
                
                for (ItemEntity item : items) {
                    if (random.nextFloat() < charge * 0.3f) {
                        // Duplicate item
                        ItemEntity duplicate = new ItemEntity(level, item.getX(), item.getY(), item.getZ(), 
                            item.getItem().copy());
                        level.addFreshEntity(duplicate);
                    }
                }
            }
        },
        
        CRITICAL_ENHANCEMENT {
            @Override
            void apply(Level level, Player player, int radius, float charge, RandomSource random) {
                // Make projectiles always crit
                AABB area = player.getBoundingBox().inflate(radius);
                List<AbstractArrow> arrows = level.getEntitiesOfClass(AbstractArrow.class, area);
                
                for (AbstractArrow arrow : arrows) {
                    arrow.setCritArrow(true);
                    arrow.setBaseDamage(arrow.getBaseDamage() * (1 + charge));
                }
            }
        },
        
        EXPERIENCE_BURST {
            @Override
            void apply(Level level, Player player, int radius, float charge, RandomSource random) {
                if (random.nextFloat() < charge * 0.5f) {
                    int orbs = random.nextInt(5) + (int)(charge * 10);
                    for (int i = 0; i < orbs; i++) {
                        ExperienceOrb orb = new ExperienceOrb(level, 
                            player.getX() + (random.nextDouble() - 0.5) * 2,
                            player.getY() + 1,
                            player.getZ() + (random.nextDouble() - 0.5) * 2,
                            random.nextInt(5) + 1);
                        level.addFreshEntity(orb);
                    }
                }
            }
        },
        
        QUANTUM_TUNNELING {
            @Override
            void apply(Level level, Player player, int radius, float charge, RandomSource random) {
                if (random.nextFloat() < charge * 0.2f) {
                    // Random short-range teleport
                    double angle = random.nextDouble() * Math.PI * 2;
                    double distance = 3 + random.nextDouble() * 5;
                    
                    double x = player.getX() + Math.cos(angle) * distance;
                    double z = player.getZ() + Math.sin(angle) * distance;
                    double y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, 
                        (int)x, (int)z);
                    
                    player.teleportTo(x, y, z);
                    level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT, 
                        SoundSource.PLAYERS, 0.5f, 1.5f);
                }
            }
        },
        
        ENTROPIC_CASCADE {
            @Override
            void apply(Level level, Player player, int radius, float charge, RandomSource random) {
                // Random block shuffling in small area
                if (random.nextFloat() < charge * 0.15f) {
                    int shuffles = random.nextInt(3) + 1;
                    for (int i = 0; i < shuffles; i++) {
                        int x1 = (int)(player.getX() + random.nextInt(7) - 3);
                        int y1 = (int)(player.getY() + random.nextInt(5) - 2);
                        int z1 = (int)(player.getZ() + random.nextInt(7) - 3);
                        
                        int x2 = (int)(player.getX() + random.nextInt(7) - 3);
                        int y2 = (int)(player.getY() + random.nextInt(5) - 2);
                        int z2 = (int)(player.getZ() + random.nextInt(7) - 3);
                        
                        BlockState state1 = level.getBlockState(new net.minecraft.core.BlockPos(x1, y1, z1));
                        BlockState state2 = level.getBlockState(new net.minecraft.core.BlockPos(x2, y2, z2));
                        
                        if (!state1.isAir() && !state2.isAir() && 
                            state1.getDestroySpeed(level, new net.minecraft.core.BlockPos(x1, y1, z1)) >= 0 &&
                            state2.getDestroySpeed(level, new net.minecraft.core.BlockPos(x2, y2, z2)) >= 0) {
                            
                            level.setBlock(new net.minecraft.core.BlockPos(x1, y1, z1), state2, 3);
                            level.setBlock(new net.minecraft.core.BlockPos(x2, y2, z2), state1, 3);
                        }
                    }
                }
            }
        };
        
        abstract void apply(Level level, Player player, int radius, float charge, RandomSource random);
    }
}