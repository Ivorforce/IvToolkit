/*
 * Copyright 2015 Lukas Tenbrink
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

package ivorius.ivtoolkit.rendering.grid;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Collection;

/**
 * Created by lukas on 21.03.15.
 */
public class CubeMesh
{

    public static void renderSides(Collection<ForgeDirection> sides, double x1, double y1, double z1, double x2, double y2, double z2, IIcon icon)
    {
        for (ForgeDirection direction : sides)
            renderSide(direction, x1,y1, z1, x2, y2, z2, icon);
    }

    public static void renderSide(ForgeDirection side, double x1, double y1, double z1, double x2, double y2, double z2, IIcon icon)
    {
        switch (side)
        {
            case NORTH:
                renderNorth(x1, y1, x2, y2, z1, icon);
                break;
            case EAST:
                renderEast(z1, y1, z2, y2, x2, icon);
                break;
            case SOUTH:
                renderSouth(x1, y1, x2, y2, z2, icon);
                break;
            case WEST:
                renderWest(z1, y1, z2, y2, x1, icon);
                break;
            case UP:
                renderUp(x1, z1, x2, z2, y2, icon);
                break;
            case DOWN:
                renderDown(x1, z1, x2, z2, y1, icon);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void renderNorth(double x1, double y1, double x2, double y2, double z, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x1, y1, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x1, y2, z, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x2, y2, z, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x2, y1, z, icon.getMinU(), icon.getMaxV());
    }

    public static void renderEast(double z1, double y1, double z2, double y2, double x, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x, y1, z1, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y2, z1, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, y2, z2, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, y1, z2, icon.getMaxU(), icon.getMinV());
    }

    public static void renderSouth(double x1, double y1, double x2, double y2, double z, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x1, y1, z, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x2, y1, z, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x2, y2, z, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x1, y2, z, icon.getMaxU(), icon.getMinV());
    }

    public static void renderWest(double z1, double y1, double z2, double y2, double x, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x, y1, z1, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y1, z2, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x, y2, z2, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x, y2, z1, icon.getMinU(), icon.getMaxV());
    }

    public static void renderUp(double x1, double z1, double x2, double z2, double y, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x1, y, z1, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x1, y, z2, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(x2, y, z2, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x2, y, z1, icon.getMaxU(), icon.getMinV());
    }

    public static void renderDown(double x1, double z1, double x2, double z2, double y, IIcon icon)
    {
        Tessellator tessellator = Tessellator.instance;
        tessellator.addVertexWithUV(x1, y, z1, icon.getMinU(), icon.getMinV());
        tessellator.addVertexWithUV(x2, y, z1, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(x2, y, z2, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(x1, y, z2, icon.getMinU(), icon.getMaxV());
    }
}
