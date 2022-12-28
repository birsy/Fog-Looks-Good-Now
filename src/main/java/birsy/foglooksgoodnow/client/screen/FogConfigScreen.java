package birsy.foglooksgoodnow.client.screen;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.client.screen.widgets.FogDensitySliders;
import birsy.foglooksgoodnow.client.screen.widgets.TickBox;
import birsy.foglooksgoodnow.config.FogLooksGoodNowOptions;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Optional;

public class FogConfigScreen extends Screen {
    public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(FogLooksGoodNowMod.MODID,"textures/gui/widgets.png");
    private final Screen lastScreen;
    private final FogLooksGoodNowOptions options;
    private FogOptionsList list;

    public FogConfigScreen(Screen pLastScreen, FogLooksGoodNowOptions options) {
        super(Component.translatable("foglooksgoodnow.options.title"));
        this.lastScreen = pLastScreen;
        this.options = options;
    }

    @Override
    protected void init() {
        super.init();
        this.list = new FogOptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);

        this.addRenderableWidget(new TickBox(this.width / 2 - 125, this.height - 100, 70, Component.literal("Use Fog"), (box) -> {}, true));

        TickBox caveFogAffectsSkyBox = new TickBox(this.width / 2 - 125, this.height - 60, 100, Component.literal("Cave Fog Affects Sky"), (box) -> {}, true);
        TickBox useCaveFog = new TickBox(this.width / 2 - 125, this.height - 80, 100, Component.literal("Use Cave Fog"), (box) -> {
            caveFogAffectsSkyBox.setEnabled(box.isTicked);
        }, true);
        caveFogAffectsSkyBox.setEnabled(useCaveFog.isTicked);

        this.addRenderableWidget(useCaveFog);
        this.addRenderableWidget(caveFogAffectsSkyBox);

        this.addRenderableWidget(new FogDensitySliders(this.width / 2 - 125, this.height - 200, Component.literal("Use Fog"), 0.5, 0.5));

        this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, (p_96257_) -> this.minecraft.setScreen(this.lastScreen)));
        this.addWidget(list);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        this.list.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    }

    public void removed() {
        this.minecraft.options.save();
    }

    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    public static List<FormattedCharSequence> tooltipAt(OptionsList pList, int pMouseX, int pMouseY) {
        Optional<AbstractWidget> optional = pList.getMouseOver(pMouseX, pMouseY);
        return optional.isPresent() && optional.get() instanceof TooltipAccessor ? ((TooltipAccessor)optional.get()).getTooltip() : ImmutableList.of();
    }
}
