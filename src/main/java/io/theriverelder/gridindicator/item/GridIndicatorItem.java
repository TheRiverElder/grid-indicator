package io.theriverelder.gridindicator.item;

import io.theriverelder.gridindicator.GridIndicator;
import io.theriverelder.gridindicator.data.GridIndicatorInfo;
import io.theriverelder.gridindicator.screen.GridIndicatorScreenHandler;
import io.theriverelder.gridindicator.utils.InventoryUtils;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.*;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Items;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static io.theriverelder.gridindicator.TranslationKeys.*;

public class GridIndicatorItem extends Item implements ExtendedScreenHandlerFactory {

    public static final Style STYLE_YELLOW = Style.EMPTY.withColor(0xffff00).withUnderline(true);
    public static final Style STYLE_RED = Style.EMPTY.withColor(0xff0000).withUnderline(true);

    public static final String KEY_INFO = "info";
    public static final String KEY_LIGHT_SOURCE = "light_source";
    public static final String KEY_PATTERN_UNIT = "pattern_unit";
    public static final String KEY_ORIGIN_POINT = "origin_point";
    public static final String KEY_RANGE_BOTTOM = "range_bottom";
    public static final String KEY_RANGE_TOP = "range_top";

    public GridIndicatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.isSneaking()) return super.use(world, user, hand);

        ItemStack stack = user.getStackInHand(hand);

        user.openHandledScreen(this);
        return TypedActionResult.success(stack);
    }


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
                player.sendMessage(new TranslatableText(SET_LIGHT_SOURCE, new TranslatableText(lightSourceItem.getTranslationKey()).setStyle(STYLE_YELLOW)), true);
            }

            info.setToStack(stack);
        } else { // 若不潜行，则放置光源
            Item lightSourceItem = Registry.ITEM.get(info.getLightSource());
            if (lightSourceItem != Items.AIR) {
                ItemStack lightSourceStack = player.isCreative()
                        ? new ItemStack(lightSourceItem, lightSourceItem.getMaxCount())
                        : InventoryUtils.getStackWithItem(player.getInventory(), lightSourceItem);
                if (lightSourceStack == null) {
                    if (world.isClient()) {
                        player.sendMessage(new TranslatableText(NOT_ENOUGH_LIGHT_SOURCE, new TranslatableText(lightSourceItem.getTranslationKey()).setStyle(STYLE_RED)), true);
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
//                                    world.updateNeighbors(posToPlace, lightSourceBlock);
                                    lightSourceStack.setCount(lightSourceStack.getCount() - 1);
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
        return prevState.getBlock() instanceof FluidBlock;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        GridIndicatorInfo info = GridIndicatorInfo.getFromStack(stack);
        tooltip.add(new TranslatableText("desc.grid_indicator.grid_indicator_pattern", info.getPatternUnit()));
        tooltip.add(new TranslatableText("desc.grid_indicator.grid_indicator_origin_point", info.getOriginPoint().getX(), info.getOriginPoint().getZ()));
        tooltip.add(new TranslatableText("desc.grid_indicator.grid_indicator_range", info.getRangeBottom(), info.getRangeTop()));
    }

    private final Text guiName = new TranslatableText("item." + GridIndicator.ID + ".grid_indicator");

    @Override
    public Text getDisplayName() {
        return guiName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new GridIndicatorScreenHandler(syncId, inv, player.getMainHandStack(), player.getInventory().selectedSlot);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeItemStack(player.getMainHandStack()).writeInt(player.getInventory().selectedSlot);
    }
}
