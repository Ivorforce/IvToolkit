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

import ivorius.ivtoolkit.rendering.Icon;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.EnumFacing;

import java.util.Collection;

/**
 * Created by lukas on 21.03.15.
 */
public class CubeMesh
{
    public static void renderSides(Collection<EnumFacing> sides, double x1, double y1, double z1, double x2, double y2, double z2, Icon icon)
    {
        for (EnumFacing direction : sides)
            renderSide(direction, x1,y1, z1, x2, y2, z2, icon);
    }

    public static void renderSide(EnumFacing side, double x1, double y1, double z1, double x2, double y2, double z2, Icon icon)
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

    public static void renderNorth(double x1, double y1, double x2, double y2, double z, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x1, y1, z).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x1, y2, z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        renderer.pos(x2, y2, z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x2, y1, z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
    }

    public static void renderEast(double z1, double y1, double z2, double y2, double x, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x, y1, z1).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x, y2, z1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        renderer.pos(x, y2, z2).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x, y1, z2).tex(icon.getMaxU(), icon.getMinV()).endVertex();
    }

    public static void renderSouth(double x1, double y1, double x2, double y2, double z, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x1, y1, z).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x2, y1, z).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        renderer.pos(x2, y2, z).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x1, y2, z).tex(icon.getMaxU(), icon.getMinV()).endVertex();
    }

    public static void renderWest(double z1, double y1, double z2, double y2, double x, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x, y1, z1).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x, y1, z2).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        renderer.pos(x, y2, z2).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x, y2, z1).tex(icon.getMinU(), icon.getMaxV()).endVertex();
    }

    public static void renderUp(double x1, double z1, double x2, double z2, double y, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x1, y, z1).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x1, y, z2).tex(icon.getMinU(), icon.getMaxV()).endVertex();
        renderer.pos(x2, y, z2).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x2, y, z1).tex(icon.getMaxU(), icon.getMinV()).endVertex();
    }

    public static void renderDown(double x1, double z1, double x2, double z2, double y, Icon icon)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        renderer.pos(x1, y, z1).tex(icon.getMinU(), icon.getMinV()).endVertex();
        renderer.pos(x2, y, z1).tex(icon.getMaxU(), icon.getMinV()).endVertex();
        renderer.pos(x2, y, z2).tex(icon.getMaxU(), icon.getMaxV()).endVertex();
        renderer.pos(x1, y, z2).tex(icon.getMinU(), icon.getMaxV()).endVertex();
    }
}
