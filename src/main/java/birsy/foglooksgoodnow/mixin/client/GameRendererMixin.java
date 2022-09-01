package birsy.foglooksgoodnow.mixin.client;

import birsy.foglooksgoodnow.FogLooksGoodNowMod;
import birsy.foglooksgoodnow.client.FogDensityManager;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tick(CallbackInfo info) {
        FogDensityManager.getDensityManagerOptional().ifPresent((fogDensityManager -> fogDensityManager.tick()));
    }
}
