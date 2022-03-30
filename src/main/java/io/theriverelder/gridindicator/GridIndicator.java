package io.theriverelder.gridindicator;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static io.theriverelder.gridindicator.item.Items.GRID_INDICATOR;


public class GridIndicator implements ModInitializer {

    public static final String ID = "grid_indicator";

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier(ID, "grid_indicator"), GRID_INDICATOR);
    }
}
