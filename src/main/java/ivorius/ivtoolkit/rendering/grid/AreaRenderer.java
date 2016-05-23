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
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.GlStateManager;
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

        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX, centerY, centerZ);
        if (lined)
        {
            GlStateManager.disableTexture2D();
            drawLineCuboid(Tessellator.getInstance().getWorldRenderer(), width2 + sizeP, height2 + sizeP, length2 + sizeP, 1);
            GlStateManager.enableTexture2D();
        }
        else
            drawCuboid(Tessellator.getInstance().getWorldRenderer(), width2 * sizeCE + sizeP, height2 * sizeCE + sizeP, length2 * sizeCE + sizeP, 1);
        GlStateManager.popMatrix();
    }

    public static void drawCuboid(WorldRenderer renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        renderer.pos(-sizeX * in, -sizeY * in, -sizeZ).tex(0, 0).endVertex();
        renderer.pos(-sizeX * in, sizeY * in, -sizeZ).tex(0, 1).endVertex();
        renderer.pos(sizeX * in, sizeY * in, -sizeZ).tex(1, 1).endVertex();
        renderer.pos(sizeX * in, -sizeY * in, -sizeZ).tex(1, 0).endVertex();

        renderer.pos(-sizeX * in, -sizeY * in, sizeZ).tex(0, 0).endVertex();
        renderer.pos(sizeX * in, -sizeY * in, sizeZ).tex(1, 0).endVertex();
        renderer.pos(sizeX * in, sizeY * in, sizeZ).tex(1, 1).endVertex();
        renderer.pos(-sizeX * in, sizeY * in, sizeZ).tex(0, 1).endVertex();

        renderer.pos(-sizeX, -sizeY * in, -sizeZ * in).tex(0, 0).endVertex();
        renderer.pos(-sizeX, -sizeY * in, sizeZ * in).tex(0, 1).endVertex();
        renderer.pos(-sizeX, sizeY * in, sizeZ * in).tex(1, 1).endVertex();
        renderer.pos(-sizeX, sizeY * in, -sizeZ * in).tex(1, 0).endVertex();

        renderer.pos(sizeX, -sizeY * in, -sizeZ * in).tex(0, 0).endVertex();
        renderer.pos(sizeX, sizeY * in, -sizeZ * in).tex(0, 1).endVertex();
        renderer.pos(sizeX, sizeY * in, sizeZ * in).tex(1, 1).endVertex();
        renderer.pos(sizeX, -sizeY * in, sizeZ * in).tex(1, 0).endVertex();

        renderer.pos(-sizeX * in, sizeY, -sizeZ * in).tex(0, 0).endVertex();
        renderer.pos(-sizeX * in, sizeY, sizeZ * in).tex(0, 1).endVertex();
        renderer.pos(sizeX * in, sizeY, sizeZ * in).tex(1, 1).endVertex();
        renderer.pos(sizeX * in, sizeY, -sizeZ * in).tex(1, 0).endVertex();

        renderer.pos(-sizeX * in, -sizeY, -sizeZ * in).tex(0, 0).endVertex();
        renderer.pos(sizeX * in, -sizeY, -sizeZ * in).tex(1, 0).endVertex();
        renderer.pos(sizeX * in, -sizeY, sizeZ * in).tex(1, 1).endVertex();
        renderer.pos(-sizeX * in, -sizeY, sizeZ * in).tex(0, 1).endVertex();

        Tessellator.getInstance().draw();
    }

    public static void drawLineCuboid(WorldRenderer renderer, float sizeX, float sizeY, float sizeZ, float in)
    {
        renderer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

        renderer.pos(-sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.pos(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.pos(sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.pos(sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.pos(-sizeX * in, -sizeY * in, -sizeZ).endVertex();

        renderer.pos(-sizeX * in, -sizeY * in, sizeZ).endVertex();
        renderer.pos(-sizeX * in, sizeY * in, sizeZ).endVertex();
        renderer.pos(sizeX * in, sizeY * in, sizeZ).endVertex();
        renderer.pos(sizeX * in, -sizeY * in, sizeZ).endVertex();
        renderer.pos(-sizeX * in, -sizeY * in, sizeZ).endVertex();

        Tessellator.getInstance().draw();

        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        renderer.pos(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.pos(-sizeX * in, sizeY * in, sizeZ).endVertex();

        renderer.pos(sizeX * in, sizeY * in, -sizeZ).endVertex();
        renderer.pos(sizeX * in, sizeY * in, sizeZ).endVertex();

        renderer.pos(sizeX * in, -sizeY * in, -sizeZ).endVertex();
        renderer.pos(sizeX * in, -sizeY * in, sizeZ).endVertex();

        Tessellator.getInstance().draw();
    }
}
