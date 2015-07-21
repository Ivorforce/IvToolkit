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

import ivorius.ivtoolkit.blocks.BlockArea;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 09.02.15.
 */
@SideOnly(Side.CLIENT)
public class AreaRenderer
{
    public static void renderAreaLined(BlockArea area, float sizeP)
    {
        renderArea(area, true, false, sizeP);
    }

    public static void renderArea(BlockArea area, boolean lined, boolean insides, float sizeP)
    {
        drawCuboid(area.getLowerCorner(), area.getHigherCorner().add(1, 1, 1), lined, insides, sizeP);
    }

    @SideOnly(Side.CLIENT)
    private static void drawCuboid(BlockPos min, BlockPos max, boolean lined, boolean insides, float sizeP)
    {
        float width2 = ((float) max.getX() - (float) min.getX()) * 0.5f;
        float height2 = ((float) max.getY() - (float) min.getY()) * 0.5f;
        float length2 = ((float) max.getZ() - (float) min.getZ()) * 0.5f;

        double centerX = min.getX() + width2;
        double centerY = min.getY() + height2;
        double centerZ = min.getZ() + length2;

        int sizeCE = insides ? -1 : 1;

        GL11.glPushMatrix();
        GL11.glTranslated(centerX, centerY, centerZ);
        if (lined)
        {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            drawLineCuboid(Tessellator.getInstance().getWorldRenderer(), width2 + sizeP, height2 + sizeP, length2 + sizeP, 1);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
        else
            drawCuboid(Tessellator.getInstance().getWorldRenderer(), width2 * sizeCE + sizeP, height2 * sizeCE + sizeP, length2 * sizeCE + sizeP, 1);
        GL11.glPopMatrix();
    }

    public static void drawCuboid(WorldRenderer renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.startDrawingQuads();

        renderer.addVertexWithUV(-sizeX * in, -sizeY * in, -sizeZ, 0, 0);
        renderer.addVertexWithUV(-sizeX * in, sizeY * in, -sizeZ, 0, 1);
        renderer.addVertexWithUV(sizeX * in, sizeY * in, -sizeZ, 1, 1);
        renderer.addVertexWithUV(sizeX * in, -sizeY * in, -sizeZ, 1, 0);

        renderer.addVertexWithUV(-sizeX * in, -sizeY * in, sizeZ, 0, 0);
        renderer.addVertexWithUV(sizeX * in, -sizeY * in, sizeZ, 1, 0);
        renderer.addVertexWithUV(sizeX * in, sizeY * in, sizeZ, 1, 1);
        renderer.addVertexWithUV(-sizeX * in, sizeY * in, sizeZ, 0, 1);

        renderer.addVertexWithUV(-sizeX, -sizeY * in, -sizeZ * in, 0, 0);
        renderer.addVertexWithUV(-sizeX, -sizeY * in, sizeZ * in, 0, 1);
        renderer.addVertexWithUV(-sizeX, sizeY * in, sizeZ * in, 1, 1);
        renderer.addVertexWithUV(-sizeX, sizeY * in, -sizeZ * in, 1, 0);

        renderer.addVertexWithUV(sizeX, -sizeY * in, -sizeZ * in, 0, 0);
        renderer.addVertexWithUV(sizeX, sizeY * in, -sizeZ * in, 0, 1);
        renderer.addVertexWithUV(sizeX, sizeY * in, sizeZ * in, 1, 1);
        renderer.addVertexWithUV(sizeX, -sizeY * in, sizeZ * in, 1, 0);

        renderer.addVertexWithUV(-sizeX * in, sizeY, -sizeZ * in, 0, 0);
        renderer.addVertexWithUV(-sizeX * in, sizeY, sizeZ * in, 0, 1);
        renderer.addVertexWithUV(sizeX * in, sizeY, sizeZ * in, 1, 1);
        renderer.addVertexWithUV(sizeX * in, sizeY, -sizeZ * in, 1, 0);

        renderer.addVertexWithUV(-sizeX * in, -sizeY, -sizeZ * in, 0, 0);
        renderer.addVertexWithUV(sizeX * in, -sizeY, -sizeZ * in, 1, 0);
        renderer.addVertexWithUV(sizeX * in, -sizeY, sizeZ * in, 1, 1);
        renderer.addVertexWithUV(-sizeX * in, -sizeY, sizeZ * in, 0, 1);

        Tessellator.getInstance().draw();
    }

    public static void drawLineCuboid(WorldRenderer renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.startDrawing(GL11.GL_LINE_STRIP);

        renderer.addVertex(-sizeX * in, -sizeY * in, -sizeZ);
        renderer.addVertex(-sizeX * in, sizeY * in, -sizeZ);
        renderer.addVertex(sizeX * in, sizeY * in, -sizeZ);
        renderer.addVertex(sizeX * in, -sizeY * in, -sizeZ);
        renderer.addVertex(-sizeX * in, -sizeY * in, -sizeZ);

        renderer.addVertex(-sizeX * in, -sizeY * in, sizeZ);
        renderer.addVertex(-sizeX * in, sizeY * in, sizeZ);
        renderer.addVertex(sizeX * in, sizeY * in, sizeZ);
        renderer.addVertex(sizeX * in, -sizeY * in, sizeZ);
        renderer.addVertex(-sizeX * in, -sizeY * in, sizeZ);

        Tessellator.getInstance().draw();

        renderer.startDrawing(GL11.GL_LINES);

        renderer.addVertex(-sizeX * in, sizeY * in, -sizeZ);
        renderer.addVertex(-sizeX * in, sizeY * in, sizeZ);

        renderer.addVertex(sizeX * in, sizeY * in, -sizeZ);
        renderer.addVertex(sizeX * in, sizeY * in, sizeZ);

        renderer.addVertex(sizeX * in, -sizeY * in, -sizeZ);
        renderer.addVertex(sizeX * in, -sizeY * in, sizeZ);

        Tessellator.getInstance().draw();
    }
}
