package com.formlesslab.ae2additions.block.reaction;

import ae2.block.AEBaseTileBlock;
import com.formlesslab.ae2additions.AppliedAdditions;
import com.formlesslab.ae2additions.init.ModGuiHandler;
import com.formlesslab.ae2additions.tile.TileReactionChamber;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;

public class BlockReactionChamber extends AEBaseTileBlock<TileReactionChamber> {
    public static final PropertyBool WORKING = PropertyBool.create("working");

    public BlockReactionChamber() {
        super(Material.IRON);
        this.setTileEntity(TileReactionChamber.class);
        this.setHardness(2.2F);
        this.setResistance(10.0F);
        this.setLightOpacity(0);
        this.useNeighborBrightness = true;
        this.setDefaultState(this.blockState.getBaseState().withProperty(WORKING, false));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return createBlockState(WORKING);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(WORKING) ? 1 : 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(WORKING, (meta & 1) == 1);
    }

    @Override
    protected IBlockState updateBlockStateFromTileEntity(IBlockState currentState, TileReactionChamber tileEntity) {
        return currentState.withProperty(WORKING, tileEntity.isWorking());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileReactionChamber tile = this.getTileEntity(world, pos);
        if (tile == null) {
            return true;
        }

        ItemStack held = player.getHeldItem(hand);
        if (!held.isEmpty() && FluidUtil.interactWithFluidHandler(player, hand, tile.getFluidHandler())) {
            return true;
        }

        if (!world.isRemote) {
            player.openGui(AppliedAdditions.INSTANCE, ModGuiHandler.REACTION_CHAMBER, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, world, pos, blockIn, fromPos);
        TileReactionChamber tile = this.getTileEntity(world, pos);
        if (tile != null) {
            tile.updateNeighbors();
        }
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }
}
