package com.formlesslab.ae2additions.tile;

import ae2.api.inventories.InternalInventory;
import net.minecraft.item.ItemStack;

import java.util.List;

final class PatternInventoryDrops {
    private PatternInventoryDrops() {
    }

    static void drain(InternalInventory inventory, List<ItemStack> drops) {
        for (int slot = 0; slot < inventory.size(); slot++) {
            ItemStack pattern = inventory.getStackInSlot(slot);
            if (!pattern.isEmpty()) {
                drops.add(pattern.copy());
                inventory.setItemDirect(slot, ItemStack.EMPTY);
            }
        }
    }
}
