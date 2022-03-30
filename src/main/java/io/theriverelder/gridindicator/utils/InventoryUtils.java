package io.theriverelder.gridindicator.utils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class InventoryUtils {

    @Nullable
    public static ItemStack getStackWithItem(PlayerInventory inventory, Item item) {
        for (ItemStack stack : inventory.main) {
            if (stack != null && stack.isOf(item) && !stack.isEmpty()) return stack;
        }
        return null;
    }
    
}
