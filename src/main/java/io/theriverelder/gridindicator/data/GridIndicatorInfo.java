package io.theriverelder.gridindicator.data;

import io.theriverelder.gridindicator.utils.NbtUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static io.theriverelder.gridindicator.item.GridIndicatorItem.*;

public class GridIndicatorInfo {

    private Identifier lightSource;
    private int patternUnit;
    private BlockPos originPoint;
    private int rangeBottom;

    private int rangeTop;

    public static GridIndicatorInfo getFromStack(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateSubNbt(KEY_INFO);
        return new GridIndicatorInfo(
                new Identifier(NbtUtils.getStringOr(nbt, KEY_LIGHT_SOURCE, "minecraft:air")),
                NbtUtils.getIntOr(nbt, KEY_PATTERN_UNIT, 4),
                new BlockPos(NbtUtils.getVec3iOr(nbt, KEY_ORIGIN_POINT, Vec3i.ZERO)),
                NbtUtils.getIntOr(nbt, KEY_RANGE_BOTTOM, 0),
                NbtUtils.getIntOr(nbt, KEY_RANGE_TOP, 0)
        );
    }

    public void setToStack(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateSubNbt(KEY_INFO);
        nbt.putString(KEY_LIGHT_SOURCE, lightSource.toString());
        nbt.putInt(KEY_PATTERN_UNIT, patternUnit);
        nbt.putIntArray(KEY_ORIGIN_POINT, new int[] {originPoint.getX(), originPoint.getY(), originPoint.getZ()});
        nbt.putInt(KEY_RANGE_BOTTOM, rangeBottom);
        nbt.putInt(KEY_RANGE_TOP, rangeTop);
    }

    public GridIndicatorInfo(Identifier lightSource, int patternUnit, BlockPos originPoint, int rangeBottom, int rangeTop) {
        this.lightSource = lightSource;
        this.patternUnit = patternUnit;
        this.originPoint = originPoint;
        this.rangeBottom = rangeBottom;
        this.rangeTop = rangeTop;
    }

    public Identifier getLightSource() {
        return lightSource;
    }

    public void setLightSource(Identifier lightSource) {
        this.lightSource = lightSource;
    }

    public int getPatternUnit() {
        return patternUnit;
    }

    public void setPatternUnit(int patternUnit) {
        this.patternUnit = patternUnit;
    }

    public BlockPos getOriginPoint() {
        return originPoint;
    }

    public void setOriginPoint(BlockPos originPoint) {
        this.originPoint = originPoint;
    }

    public int getRangeBottom() {
        return rangeBottom;
    }

    public void setRangeBottom(int rangeBottom) {
        this.rangeBottom = rangeBottom;
    }

    public int getRangeTop() {
        return rangeTop;
    }

    public void setRangeTop(int rangeTop) {
        this.rangeTop = rangeTop;
    }

    public GridIndicatorInfo copy() {
        return new GridIndicatorInfo(lightSource, patternUnit, originPoint, rangeBottom, rangeTop);
    }
}
