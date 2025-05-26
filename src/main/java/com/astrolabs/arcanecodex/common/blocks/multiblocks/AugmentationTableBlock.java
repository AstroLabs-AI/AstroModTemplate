package com.astrolabs.arcanecodex.common.blocks.multiblocks;

import com.astrolabs.arcanecodex.common.blockentities.AugmentationTableBlockEntity;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AugmentationTableBlock extends BaseEntityBlock {
    
    public static final BooleanProperty MULTIBLOCK_FORMED = BooleanProperty.create("formed");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);
    
    public AugmentationTableBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MULTIBLOCK_FORMED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MULTIBLOCK_FORMED);
    }
    
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AugmentationTableBlockEntity(pos, state);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AugmentationTableBlockEntity blockEntity) {
            if (blockEntity.checkMultiblockStructure()) {
                NetworkHooks.openScreen((ServerPlayer) player, blockEntity, pos);
            } else {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Multiblock structure incomplete!"));
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Requires Neural Matrix blocks at corners"));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AugmentationTableBlockEntity blockEntity) {
            blockEntity.checkMultiblockStructure();
        }
    }
    
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AugmentationTableBlockEntity blockEntity) {
            blockEntity.checkMultiblockStructure();
        }
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, ModBlockEntities.AUGMENTATION_TABLE.get(), AugmentationTableBlockEntity::clientTick);
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.AUGMENTATION_TABLE.get(), AugmentationTableBlockEntity::serverTick);
    }
    
    public static boolean isValidMultiblockPosition(Level level, BlockPos centerPos) {
        // Check for Neural Matrix blocks at the four corners
        for (Direction horizontal : Direction.Plane.HORIZONTAL) {
            BlockPos cornerPos = centerPos.relative(horizontal).relative(horizontal.getClockWise());
            BlockState cornerState = level.getBlockState(cornerPos);
            
            // TODO: Check for Neural Matrix block when implemented
            if (!cornerState.isAir()) {
                return false;
            }
        }
        return true;
    }
}