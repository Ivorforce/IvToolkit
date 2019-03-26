/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ivorius.ivtoolkit.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiSlider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 28.05.14.
 */
public class IvGuiSlider extends GuiSlider
{
    private List<GuiControlListener<IvGuiSlider>> listeners = new ArrayList<>();

    public IvGuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr)
    {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr);
    }

    public IvGuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, @Nullable ISlider par)
    {
        super(id, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
    }

    public IvGuiSlider(int id, int xPos, int yPos, String displayStr, double minVal, double maxVal, double currentVal, ISlider par)
    {
        super(id, xPos, yPos, displayStr, minVal, maxVal, currentVal, par);
    }

    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        super.onRelease(mouseX, mouseY);

        for (GuiControlListener<IvGuiSlider> listener : listeners)
        {
            listener.valueChanged(this);
        }
    }
}