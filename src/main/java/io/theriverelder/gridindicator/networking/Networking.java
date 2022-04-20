package io.theriverelder.gridindicator.networking;

import io.theriverelder.gridindicator.GridIndicator;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class Networking {

    public static final Identifier CHANNEL_UPDATE_STACK = new Identifier(GridIndicator.ID, "update_stack");

    public static void registerServerReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_UPDATE_STACK, Networking::handleUpdateStackPacket);
    }

    public static void handleUpdateStackPacket(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        ItemStack stack = buf.readItemStack();
        int slot = buf.readInt();
        server.execute(() -> player.getInventory().setStack(slot, stack));
    }

}
