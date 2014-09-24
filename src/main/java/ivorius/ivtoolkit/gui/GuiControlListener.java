/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.gui;

import net.minecraft.client.gui.Gui;

/**
 * Created by lukas on 28.05.14.
 */
public interface GuiControlListener<G extends Gui>
{
    void valueChanged(G gui);
}
