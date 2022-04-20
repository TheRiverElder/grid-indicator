package io.theriverelder.gridindicator.screen;

import io.theriverelder.gridindicator.GridIndicator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class Screens {
    public static ScreenHandlerType<GridIndicatorScreenHandler> GRID_INDICATOR_SCREEN_TYPE;

    public static void registerScreenTypes() {
        GRID_INDICATOR_SCREEN_TYPE = ScreenHandlerRegistry.registerExtended(new Identifier(GridIndicator.ID, "grid_indicator"), GridIndicatorScreenHandler::new);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {

        public static void registerScreens() {
            ScreenRegistry.register(GRID_INDICATOR_SCREEN_TYPE, GridIndicatorScreen::new);
        }

    }

}
