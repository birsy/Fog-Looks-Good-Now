package birsy.foglooksgoodnow.client.screen.widgets;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.client.screen.FogConfigScreen;
import birsy.foglooksgoodnow.util.MathUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FogDensitySliders extends AbstractWidget {
    private double[] values;

    public FogDensitySliders(int pX, int pY, Component pMessage, double fogStart, double fogEnd) {
        super(pX, pY, 243, 32, pMessage);
        this.values = new double[]{fogStart, fogEnd};
    }

    public void applyValues() {}
    public void updateMessages() {}
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}

    public void onClick(double pMouseX, double pMouseY) {
        if (this.isHoveredOrFocused()) {
            int index = getClosestSliderToMouse(pMouseX);
            //this.selectedSlider = index;
            this.setValueFromMouse(pMouseX, index);
        }
    }
    public void onRelease(double pMouseX, double pMouseY) {
       // this.selectedSlider = -1;
    }
    protected void onDrag(double pMouseX, double pMouseY, double pDragX, double pDragY) {
        this.setValueFromMouse(pMouseX, this.getClosestSliderToMouse(pMouseX));
    }

    private void setValue(double value, int index) {
        double pValue = this.values[index];
        this.values[index] = Mth.clamp(value, 0.0D, 1.0D);
        if (pValue != this.values[index]) {
            this.applyValues();
        }

        this.updateMessages();
    }
    private void setValueFromMouse(double pMouseX, int index) {
        setValue(getValueFromMouse(pMouseX), index);
    }
    private double getValueFromMouse(double pMouseX) {
        return (pMouseX - (double)(this.x)) / (double)(this.width);
    }
    private int getClosestSliderToMouse(double pMouseX) {
        double mouseValue = getValueFromMouse(pMouseX);
        double fSDist = Math.abs(mouseValue - this.values[0]);
        double fEDist = Math.abs(mouseValue - this.values[1]);
        return fSDist < fEDist ? 0 : 1;
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        renderBg(pPoseStack, Minecraft.getInstance(), pMouseX, pMouseY);
        renderControlPoints(pPoseStack, Minecraft.getInstance(), pMouseX, pMouseY);
    }

    protected void renderBg(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, FogConfigScreen.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        this.blit(pPoseStack, this.x, this.y, 0, 149, 243, 19);
    }

    private double getValueInBlocks(int index) {
        double value = values[index];
        return MathUtils.mapRange(41.5 / 243.0, 1.0, 0.0, Minecraft.getInstance().options.renderDistance().get() * 16, value);
    }

    private void renderControlPoints(PoseStack pPoseStack, Minecraft pMinecraft, int pMouseX, int pMouseY) {
        RenderSystem.setShaderTexture(0, FogConfigScreen.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
// &&
        int highlightedSlider = -1;

        int margin = 15;
        if (pMouseX > this.x - margin && pMouseX < this.x + this.width + margin
                && pMouseY > this.y - margin && pMouseY < this.y + this.height + margin) highlightedSlider = getClosestSliderToMouse(pMouseX);
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;

        for (int i = 0; i < this.values.length; i++) {
            RenderSystem.setShaderTexture(0, FogConfigScreen.WIDGETS_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int dx = (int) ( this.x + (this.values[i] * this.width) ) - 3;
            int dy = this.y + 16;
            this.blit(pPoseStack, dx, dy, i == highlightedSlider ? 7 : 0, 168, 7, 9);
            if (i == highlightedSlider) {
                drawCenteredString(pPoseStack, font, Component.literal("Fog " + (i == 0 ? "Start" : "End") + ": " + Math.round(getValueInBlocks(i)) + " Blocks"), dx, dy + 10, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
            }
        }
    }
}
