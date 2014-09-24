/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.blocks;

import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 26.07.14.
 */
public class IvRotatableBlockRenderHelper
{
    public static void transformFor(IvTileEntityRotatable tileEntity, double renderX, double renderY, double renderZ)
    {
        GL11.glTranslated(renderX + 0.5, renderY + 0.5, renderZ + 0.5);
        GL11.glRotatef(-90.0f * tileEntity.direction + 180.0f, 0.0f, 1.0f, 0.0f);
    }
}
