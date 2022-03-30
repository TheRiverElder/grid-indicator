package io.theriverelder.gridindicator;

public class TranslationKeys {

    public static final String SET_LIGHT_SOURCE = of("set_light_source");
    public static final String SET_PATTERN_UNIT = of("set_pattern_unit");
    public static final String SET_ORIGIN_POINT = of("set_origin_point");

    public static final String NOT_A_GRID_POINT = of("not_a_grid_point");
    public static final String NOT_ENOUGH_LIGHT_SOURCE = of("not_enough_light_source");

    public static String of(String path) {
        return "text." + GridIndicator.ID + "." + path;
    }

}
