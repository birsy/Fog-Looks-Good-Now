package birsy.foglooksgoodnow.mixin.client;

import birsy.foglooksgoodnow.client.screen.FogConfigScreen;
import birsy.foglooksgoodnow.config.FogLooksGoodNowOptions;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Component pTitle) {
        super(pTitle);
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        this.addRenderableWidget(new Button(this.width / 2 - 125, this.height / 6 + 168, 20, 20, Component.literal("f"), (p_96257_) -> {
            this.minecraft.setScreen(new FogConfigScreen(this, new FogLooksGoodNowOptions()));
        }));
    }
}
