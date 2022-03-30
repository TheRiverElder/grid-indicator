package io.theriverelder.gridindicator.utils;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3i;

public class NbtUtils {

    public static String getStringOr(NbtCompound nbt, String key, String defaultValue) {
        if (!nbt.contains(key)) return defaultValue;
        String result = nbt.getString(key);
        return result == null || "".equals(result) ? defaultValue : result;
    }

    public static int getIntOr(NbtCompound nbt, String key, int defaultValue) {
        if (!nbt.contains(key)) return defaultValue;
        return nbt.getInt(key);
    }

    public static Vec3i getVec3iOr(NbtCompound nbt, String key, Vec3i defaultValue) {
        if (!nbt.contains(key)) return defaultValue;
        int[] data = nbt.getIntArray(key);
        if (data.length != 3) return defaultValue;
        return new Vec3i(data[0], data[1], data[2]);
    }

}
