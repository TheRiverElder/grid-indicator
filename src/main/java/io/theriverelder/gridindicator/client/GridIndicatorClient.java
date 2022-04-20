package io.theriverelder.gridindicator.client;

import io.theriverelder.gridindicator.screen.Screens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class GridIndicatorClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Screens.Client.registerScreens();
    }
}
