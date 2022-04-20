package io.theriverelder.gridindicator.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;

import static io.theriverelder.gridindicator.screen.Screens.GRID_INDICATOR_SCREEN_TYPE;

public class GridIndicatorScreenHandler extends ScreenHandler {

    protected ItemStack stack;
    protected int slot;

    public GridIndicatorScreenHandler(int syncId, PlayerInventory inventory, ItemStack stack,int slot) {
        super(GRID_INDICATOR_SCREEN_TYPE, syncId);
        this.stack = stack;
        this.slot = slot;
    }

    public GridIndicatorScreenHandler(int syncId, PlayerInventory inventory, PacketByteBuf buf) {
        this(syncId, inventory, buf.readItemStack(), buf.readInt());
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
