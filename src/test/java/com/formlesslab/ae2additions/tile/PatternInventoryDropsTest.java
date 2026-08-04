package com.formlesslab.ae2additions.tile;

import ae2.util.inv.AppEngInternalInventory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternInventoryDropsTest {
    @Test
    void drainsEveryPatternAndClearsTheInventory() {
        AppEngInternalInventory inventory = new AppEngInternalInventory(4);
        ItemStack firstPattern = new ItemStack(Items.PAPER);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("pattern", "first");
        firstPattern.setTagCompound(tag);
        ItemStack secondPattern = new ItemStack(Items.MAP);
        inventory.setItemDirect(0, firstPattern);
        inventory.setItemDirect(3, secondPattern);

        List<ItemStack> drops = new ArrayList<>();
        PatternInventoryDrops.drain(inventory, drops);

        assertEquals(2, drops.size());
        assertEquals("first", drops.get(0).getTagCompound().getString("pattern"));
        assertNotSame(firstPattern, drops.get(0));
        assertNotSame(secondPattern, drops.get(1));
        for (int slot = 0; slot < inventory.size(); slot++) {
            assertTrue(inventory.getStackInSlot(slot).isEmpty());
        }
    }

    @Test
    void drainingTwiceDoesNotDuplicateDrops() {
        AppEngInternalInventory inventory = new AppEngInternalInventory(1);
        inventory.setItemDirect(0, new ItemStack(Items.PAPER));
        List<ItemStack> drops = new ArrayList<>();

        PatternInventoryDrops.drain(inventory, drops);
        PatternInventoryDrops.drain(inventory, drops);

        assertEquals(1, drops.size());
        assertTrue(inventory.getStackInSlot(0).isEmpty());
    }
}
