package com.astrolabs.arcanecodex.common.blocks.machines;

import com.astrolabs.arcanecodex.common.blockentities.NeuralInterfaceBlockEntity;
import com.astrolabs.arcanecodex.common.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
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

public class NeuralInterfaceBlock extends BaseEntityBlock {
    
    public static final BooleanProperty LINKED = BooleanProperty.create("linked");
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 12, 16);
    
    public NeuralInterfaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LINKED, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LINKED);
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
        return new NeuralInterfaceBlockEntity(pos, state);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof NeuralInterfaceBlockEntity blockEntity) {
            // Link to player's consciousness
            blockEntity.linkToPlayer(player);
            NetworkHooks.openScreen((ServerPlayer) player, blockEntity, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return createTickerHelper(blockEntityType, ModBlockEntities.NEURAL_INTERFACE.get(), NeuralInterfaceBlockEntity::clientTick);
        }
        return createTickerHelper(blockEntityType, ModBlockEntities.NEURAL_INTERFACE.get(), NeuralInterfaceBlockEntity::serverTick);
    }
}