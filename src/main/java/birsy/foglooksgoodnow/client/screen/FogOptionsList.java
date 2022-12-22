package birsy.foglooksgoodnow.client.screen;

import birsy.foglooksgoodnow.config.FogLooksGoodNowOptions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class FogOptionsList extends ContainerObjectSelectionList<FogOptionsList.Entry> {
    public FogOptionsList(Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1, pItemHeight);
    }

    @OnlyIn(Dist.CLIENT)
    protected static class Entry extends ContainerObjectSelectionList.Entry<FogOptionsList.Entry> {
        final Map<OptionInstance<?>, AbstractWidget> options;
        final List<AbstractWidget> children;

        private Entry(Map<OptionInstance<?>, AbstractWidget> pOptions) {
            this.options = pOptions;
            this.children = ImmutableList.copyOf(pOptions.values());
        }

        public void render(PoseStack pPoseStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
            this.children.forEach((p_94494_) -> {
                p_94494_.y = pTop;
                p_94494_.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
            });
        }

        public List<? extends GuiEventListener> children() {
            return this.children;
        }

        public List<? extends NarratableEntry> narratables() {
            return this.children;
        }
    }
}
