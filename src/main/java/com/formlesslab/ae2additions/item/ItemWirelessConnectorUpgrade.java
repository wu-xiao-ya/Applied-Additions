package com.formlesslab.ae2additions.item;

import com.formlesslab.ae2additions.init.ModContent;
import com.formlesslab.ae2additions.tile.TileWirelessConnector;
import com.formlesslab.ae2additions.tile.TileWirelessHub;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWirelessConnectorUpgrade extends Item {
    public ItemWirelessConnectorUpgrade() {
        this.setMaxStackSize(64);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof TileWirelessConnector connector)) {
            return EnumActionResult.PASS;
        }

        if (!world.isRemote) {
            NBTTagCompound saved = new NBTTagCompound();
            connector.saveAdditional(saved);
            saved.removeTag("proxy");
            connector.breakOnRemove();
            world.setBlockState(pos, ModContent.WIRELESS_HUB.getDefaultState(), 3);
            TileEntity newTile = world.getTileEntity(pos);
            if (newTile instanceof TileWirelessHub hub) {
                hub.loadTag(saved);
                hub.saveChanges();
                hub.markForUpdate();
            }
            ItemStack held = player.getHeldItem(hand);
            if (!player.capabilities.isCreativeMode) {
                held.shrink(1);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}
