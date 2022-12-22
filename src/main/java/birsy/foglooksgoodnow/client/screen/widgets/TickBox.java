package birsy.foglooksgoodnow.client.screen.widgets;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.client.screen.FogConfigScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

public class TickBox extends AbstractButton {
    public static final TickBox.OnTooltip NO_TOOLTIP = (button, poseStack, mouseX, mouseY) -> {};
    protected final OnTick onTick;
    protected final TickBox.OnTooltip onTooltip;
    public boolean isTicked;
    private boolean isEnabled = true;

    public TickBox(int pX, int pY, int pWidth, Component pMessage, OnTick pOnTick, OnTooltip pOnTooltip, boolean pIsTicked) {
        super(pX, pY, pWidth, 10, pMessage);
        this.onTick = pOnTick;
        this.isTicked = pIsTicked;
        this.onTooltip = pOnTooltip;
    }

    public TickBox(int pX, int pY, int pWidth, Component pMessage, OnTick pOnTick, boolean pIsTicked) {
        this(pX, pY, pWidth, pMessage, pOnTick, NO_TOOLTIP, pIsTicked);
    }

    public void onPress() {
        if (this.isEnabled) {
            isTicked = !isTicked;
            this.onTick.onTick(this);
        }
    }

    public void setEnabled(boolean pIsEnabled) {
        this.isEnabled = pIsEnabled;
    }

    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, FogConfigScreen.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        this.blit(pPoseStack, this.x, this.y, this.isEnabled ? this.isHoveredOrFocused() ? 10 : 0 : 20, this.isTicked ? 96 : 86, 10, 10);
        this.renderBg(pPoseStack, minecraft, pMouseX, pMouseY);
        drawString(pPoseStack, font, this.getMessage(), this.x + 15, this.y + 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);
        //drawCenteredString(pPoseStack, font, this.getMessage(), this.x + (this.width - 10) / 2, this.y + (this.height - 8) / 2, getFGColor() | Mth.ceil(this.alpha * 255.0F) << 24);

        if (this.isHoveredOrFocused()) {
            this.renderToolTip(pPoseStack, pMouseX, pMouseY);
        }
    }

    public void renderToolTip(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.onTooltip.onTooltip(this, pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {
        this.defaultButtonNarrationText(pNarrationElementOutput);
        this.onTooltip.narrateTooltip((p_168841_) -> {
            pNarrationElementOutput.add(NarratedElementType.HINT, p_168841_);
        });
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnTick {
        void onTick(TickBox pButton);
    }

    @OnlyIn(Dist.CLIENT)
    public interface OnTooltip {
        void onTooltip(TickBox pButton, PoseStack pPoseStack, int pMouseX, int pMouseY);

        default void narrateTooltip(Consumer<Component> pContents) {
        }
    }
}
