package birsy.foglooksgoodnow.client.screen;

import birsy.foglooksgoodnow.client.screen.widgets.TickBox;
import birsy.foglooksgoodnow.config.FogLooksGoodNowOptions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.worldselection.EditGameRulesScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FogOptionsList extends ContainerObjectSelectionList<FogOptionsList.Entry> {

    public FogOptionsList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        FogOptionsList.Entry entry = this.getHovered();
        if (entry != null) {
            //todo: set the tooltip...
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<FogOptionsList.Entry> {
        @Nullable
        final List<FormattedCharSequence> tooltip;

        public Entry(@Nullable List<FormattedCharSequence> pTooltip) {
            this.tooltip = pTooltip;
        }

        @Nullable
        public List<FormattedCharSequence> getTooltip(int pMouseX, int pMouseY) {
            return this.tooltip;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public abstract class ConfigEntry extends FogOptionsList.Entry {
        protected final List<FormattedCharSequence> label;
        protected final List<AbstractWidget> children = Lists.newArrayList();

        public ConfigEntry(List<FormattedCharSequence> pTooltip, Component pLabel) {
            super(pTooltip);
            this.label = Minecraft.getInstance().font.split(pLabel, 175);
        }

        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }

    public class TickBoxConfigEntry extends FogOptionsList.ConfigEntry {
        private final TickBox tickbox;

        public TickBoxConfigEntry(Component pLabel, List<FormattedCharSequence> pTooltip) {
            super(pTooltip, pLabel);
            this.tickbox = new TickBox(10, 5, 100, pLabel, (box) -> {}, true);
            this.children.add(this.tickbox);
        }

        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.tickbox.x = pLeft + pWidth - 45;
            this.tickbox.y = pTop;
            this.tickbox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public class LinkedTickBoxesConfigEntry extends FogOptionsList.ConfigEntry {
        private final TickBox mainTickBox;
        private final TickBox[] subTickBoxes;

        public LinkedTickBoxesConfigEntry(List<FormattedCharSequence>[] pTooltip, Component[] labels) {
            super(pTooltip[0], labels[0]);
            this.subTickBoxes = new TickBox[labels.length];
            for (int i = 1; i < this.subTickBoxes.length; i++) {
                this.subTickBoxes[i] = new TickBox(10, 5 + (i * 20), 100, labels[i], (box) -> {}, true);
                this.children.set(i, this.subTickBoxes[i]);
            }

            this.mainTickBox = new TickBox(10, 5, 100, labels[0], (box) -> {
                for (TickBox subTickBox : this.subTickBoxes) {
                    subTickBox.setEnabled(box.isTicked);
                }
            }, true);

            this.children.set(0, this.mainTickBox);
        }

        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.mainTickBox.x = pLeft + pWidth - 45;
            this.mainTickBox.y = pTop;
            this.mainTickBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);

            for (int i = 0; i < this.subTickBoxes.length; i++) {
                TickBox subTickBox = this.subTickBoxes[i];
                subTickBox.x = pLeft + pWidth - 45;
                subTickBox.y = pTop + (20 * i);
                subTickBox.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            }
        }
    }
}
