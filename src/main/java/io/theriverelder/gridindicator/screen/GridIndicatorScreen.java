package io.theriverelder.gridindicator.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.theriverelder.gridindicator.GridIndicator;
import io.theriverelder.gridindicator.data.GridIndicatorInfo;
import io.theriverelder.gridindicator.networking.Networking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class GridIndicatorScreen extends HandledScreen<GridIndicatorScreenHandler> {

    public static final Identifier TEXTURE = new Identifier(GridIndicator.ID, "textures/gui/grid_indicator_settings.png");

    public GridIndicatorScreen(GridIndicatorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    protected ButtonWidget btnSave;

    protected TextFieldWidget tfdPatternUnit;
    protected TextFieldWidget tfdOriginPointX;
    protected TextFieldWidget tfdOriginPointZ;
    protected TextFieldWidget tfdRangeBottom;
    protected TextFieldWidget tfdRangeTop;

    protected PinnedText txtPatternUnit;
    protected PinnedText txtOriginPoint;
    protected PinnedText txtRangeBottom;
    protected PinnedText txtRangeTop;

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        GridIndicatorInfo info = GridIndicatorInfo.getFromStack(handler.stack);

        int lineHeight = 20;
        int lineGap = 5;
        int textWidth = 50;
        int textFieldWidth = 50;
        int x = this.x + (backgroundWidth - textWidth - textFieldWidth) / 2;
        int y = this.y + lineHeight;

        txtPatternUnit = new PinnedText(x, y, new TranslatableText("gui." + GridIndicator.ID + ".text.pattern_unit"));
        tfdPatternUnit = addDrawableChild(new TextFieldWidget(textRenderer, x + textWidth, y, textFieldWidth, lineHeight, new LiteralText(String.valueOf(info.getPatternUnit()))));
        tfdPatternUnit.setText(String.valueOf(info.getPatternUnit()));
        y += lineHeight + lineGap;

        txtOriginPoint = new PinnedText(x, y, new TranslatableText("gui." + GridIndicator.ID + ".text.origin_point"));
        tfdOriginPointX = addDrawableChild(new TextFieldWidget(textRenderer, x + textWidth, y, textFieldWidth / 2 - 2, lineHeight, new LiteralText(String.valueOf(info.getOriginPoint().getX()))));
        tfdOriginPointZ = addDrawableChild(new TextFieldWidget(textRenderer, x + textWidth + textFieldWidth / 2 + 2, y, textFieldWidth / 2 - 2, lineHeight, new LiteralText(String.valueOf(info.getOriginPoint().getZ()))));
        tfdOriginPointX.setText(String.valueOf(info.getOriginPoint().getX()));
        tfdOriginPointZ.setText(String.valueOf(info.getOriginPoint().getZ()));
        y += lineHeight + lineGap;

        txtRangeBottom = new PinnedText(x, y, new TranslatableText("gui." + GridIndicator.ID + ".text.range_bottom"));
        tfdRangeBottom = addDrawableChild(new TextFieldWidget(textRenderer, x + textWidth, y, textFieldWidth, lineHeight, new LiteralText(String.valueOf(info.getRangeBottom()))));
        tfdRangeBottom.setText(String.valueOf(info.getRangeBottom()));
        y += lineHeight + lineGap;

        txtRangeTop = new PinnedText(x, y, new TranslatableText("gui." + GridIndicator.ID + ".text.range_top"));
        tfdRangeTop = addDrawableChild(new TextFieldWidget(textRenderer, x + textWidth, y, textFieldWidth, lineHeight, new LiteralText(String.valueOf(info.getRangeTop()))));
        tfdRangeTop.setText(String.valueOf(info.getRangeTop()));
        y += lineHeight + lineGap;

        btnSave = addDrawableChild(new ButtonWidget(x + (textWidth + textWidth) / 4, this.y + backgroundHeight - 2 * lineHeight, (textWidth + textWidth) / 2, lineHeight, new TranslatableText("gui." + GridIndicator.ID + ".button.save"), this::onButtonSaveClicked));
    }

    protected void onButtonSaveClicked(ButtonWidget button) {
        this.save();
        this.onClose();
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
        renderTexts(matrices, mouseX, mouseY);
    }

    protected int textColor = 0x000000;

    public void renderTexts(MatrixStack matrices, int mouseX, int mouseY) {
        matrices.push();
        matrices.translate(-this.x, -this.y, 0);
        textRenderer.draw(matrices, txtPatternUnit.text, txtPatternUnit.x, txtPatternUnit.y, textColor);
        textRenderer.draw(matrices, txtOriginPoint.text, txtOriginPoint.x, txtOriginPoint.y, textColor);
        textRenderer.draw(matrices, txtRangeBottom.text, txtRangeBottom.x, txtRangeBottom.y, textColor);
        textRenderer.draw(matrices, txtRangeTop.text, txtRangeTop.x, txtRangeTop.y, textColor);
        matrices.pop();
    }

    protected void save() {
        GridIndicatorInfo info = GridIndicatorInfo.getFromStack(handler.stack);

        info.setPatternUnit(tryParseInt(tfdPatternUnit.getText(), info.getPatternUnit()));
        BlockPos prevOriginPoint = info.getOriginPoint();
        info.setOriginPoint(new BlockPos(
                tryParseInt(tfdOriginPointX.getText(), prevOriginPoint.getX()),
                prevOriginPoint.getY(),
                tryParseInt(tfdOriginPointZ.getText(), prevOriginPoint.getZ())
        ));
        info.setRangeBottom(tryParseInt(tfdRangeBottom.getText(), info.getRangeBottom()));
        info.setRangeTop(tryParseInt(tfdRangeTop.getText(), info.getRangeTop()));

        info.setToStack(handler.stack);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeItemStack(handler.stack).writeInt(handler.slot);
        ClientPlayNetworking.send(Networking.CHANNEL_UPDATE_STACK, buf);
    }

    public static int tryParseInt(String input, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(input);
        } catch (Exception ignored) {}
        return result;
    }

    static class PinnedText {
        int x;
        int y;
        Text text;

        public PinnedText(int x, int y, Text text) {
            this.x = x;
            this.y = y;
            this.text = text;
        }
    }

}
