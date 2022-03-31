package io.theriverelder.gridindicator.item;

import io.theriverelder.gridindicator.data.GridIndicatorInfo;
import io.theriverelder.gridindicator.utils.InventoryUtils;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import static io.theriverelder.gridindicator.TranslationKeys.*;

public class GridIndicatorItem extends Item {

    public static final String KEY_INFO = "info";
    public static final String KEY_LIGHT_SOURCE = "light_source";
    public static final String KEY_PATTERN_UNIT = "pattern_unit";
    public static final String KEY_ORIGIN_POINT = "origin_point";

    public GridIndicatorItem(Settings settings) {
        super(settings);
    }

//    @Override
//    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
//        ItemStack stack = user.getStackInHand(hand);
//
//        GridIndicatorInfo info = GridIndicatorInfo.getFromStack(stack);
//
//
//        return super.use(world, user, hand);
//    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();
        Direction side = context.getSide();
        if (player == null || world == null) return ActionResult.PASS;

        GridIndicatorInfo info = GridIndicatorInfo.getFromStack(stack);
        GridIndicatorInfo prevInfo = info.copy();
        info.setPatternUnit(stack.getCount());
        if (info.getPatternUnit() != prevInfo.getPatternUnit()) {
            if (world.isClient()) {
                player.sendMessage(new TranslatableText(SET_PATTERN_UNIT, new TranslatableText(SET_PATTERN_UNIT, info.getPatternUnit())), false);
            }
        }
        info.setToStack(stack);

        if (player.isSneaking()) { // 若潜行，则选中光源
            BlockState state = world.getBlockState(pos);
            Item lightSourceItem = state.getBlock().asItem();
            Identifier lightSource = Registry.ITEM.getId(lightSourceItem);

            info.setLightSource(lightSource);
            if (world.isClient()) {
                player.sendMessage(new TranslatableText(SET_LIGHT_SOURCE, new TranslatableText(lightSourceItem.getTranslationKey())), true);
            }

            info.setToStack(stack);
        } else { // 若不潜行，则放置光源
            Item lightSourceItem = Registry.ITEM.get(info.getLightSource());
            if (lightSourceItem != Items.AIR) {
                ItemStack lightSourceStack = InventoryUtils.getStackWithItem(player.getInventory(), lightSourceItem);
                if (lightSourceStack == null) {
                    if (world.isClient()) {
                        player.sendMessage(new TranslatableText(NOT_ENOUGH_LIGHT_SOURCE, new TranslatableText(lightSourceItem.getTranslationKey())), true);
                    }
                } else {
                    BlockPos posToPlace = pos.add(side.getVector());
                    if (isGridPoint(info, posToPlace)) {
                        Block lightSourceBlock = Registry.BLOCK.get(info.getLightSource());
                        if (lightSourceBlock != Blocks.AIR) {
                            ItemPlacementContext ipc = new ItemPlacementContext(context);
                            BlockState state = lightSourceBlock.getPlacementState(ipc);
                            if (canSetLightSourceBlock(world, posToPlace, state, player)) {
                                if (world.setBlockState(posToPlace, state)) {
                                    world.updateNeighbors(posToPlace, lightSourceBlock);
                                    if (!player.isCreative()) {
                                        lightSourceStack.setCount(lightSourceStack.getCount() - 1);
                                    }
                                }
                            }
                        }
                    } else {
                        if (world.isClient()) {
                            player.sendMessage(new TranslatableText(NOT_A_GRID_POINT, posToPlace.getX(), posToPlace.getZ()), true);
                        }
                    }
                }
            }
        }

        super.useOnBlock(context);
        return ActionResult.SUCCESS;
    }

    public static boolean isGridPoint(GridIndicatorInfo info, BlockPos pos) {
        int patternUnit = info.getPatternUnit();
        return pos.getX() % patternUnit == 0 && pos.getZ() % patternUnit == 0;
    }

    public static boolean canSetLightSourceBlock(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.canSetBlock(pos)) return false;
        if (!world.canPlace(state, pos, ShapeContext.of(player))) return false;

        if (world.isAir(pos)) return true;
        BlockState prevState = world.getBlockState(pos);
        if (prevState.getBlock() instanceof FluidBlock) return true;
        else return false;
    }
}
