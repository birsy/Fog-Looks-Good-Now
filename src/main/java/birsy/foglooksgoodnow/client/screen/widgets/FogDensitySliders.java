package birsy.foglooksgoodnow.client.screen.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class FogDensitySliders extends AbstractSliderButton {

    public FogDensitySliders(int pX, int pY, int pWidth, int pHeight, Component pMessage, double pValue) {
        super(pX, pY, pWidth, pHeight, pMessage, pValue);
    }

    @Override
    protected void updateMessage() {

    }

    @Override
    protected void applyValue() {

    }
}
