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

package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.tools.WorldRendererAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class IvRenderHelper
{
    public static void drawRectFullScreen(Minecraft mc)
    {
        drawRectFullScreen(mc.displayWidth, mc.displayHeight);
    }

    public static void drawRectFullScreen(int screenWidth, int screenHeight)
    {
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        renderer.pos(0.0, 0.0, 0.0).tex(0.0, 1.0);
        renderer.pos(0.0, screenHeight, 0.0).tex(0.0, 0.0);
        renderer.pos(screenWidth, screenHeight, 0.0).tex(1.0, 0.0);
        renderer.pos(screenWidth, 0.0, 0.0).tex(1.0, 1.0);
        Tessellator.getInstance().draw();
    }

    public static void renderLights(float ticks, int color, float alpha, int number)
    {
        float width = 2.5f;

        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();

        float usedTicks = ticks / 200.0F;

        Random random = new Random(432L);
        GlStateManager.disableTexture2D();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GlStateManager.enableCull();
        GlStateManager.depthMask(false);
        GlStateManager.pushMatrix();

        for (int var7 = 0; (float) var7 < number; ++var7)
        {
            float xLogFunc = (((float) var7 / number * 28493.0f + ticks) / 10.0f) % 20.0f;
            if (xLogFunc > 10.0f)
            {
                xLogFunc = 20.0f - xLogFunc;
            }

            float yLogFunc = 1.0f / (1.0f + (float) Math.pow(2.71828f, -0.8f * xLogFunc) * ((1.0f / 0.01f) - 1.0f));

            float lightAlpha = yLogFunc;

            if (lightAlpha > 0.01f)
            {
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(random.nextFloat() * 360.0F + usedTicks * 90.0F, 0.0F, 0.0F, 1.0F);
                renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_TEX_COLOR);
                float var8 = random.nextFloat() * 20.0F + 5.0F;
                float var9 = random.nextFloat() * 2.0F + 1.0F;
                renderer.color(r, g, b, alpha * lightAlpha);
                renderer.pos(0.0D, 0.0D, 0.0D).endVertex();
                renderer.color(r, g, b, 0);
                renderer.pos(-width * (double) var9, var8, (-0.5F * var9)).endVertex();
                renderer.pos(width * (double) var9, var8, (-0.5F * var9)).endVertex();
                renderer.pos(0.0D, var8, (1.0F * var9)).endVertex();
                renderer.pos(-width * (double) var9, var8, (-0.5F * var9)).endVertex();
                Tessellator.getInstance().draw();
            }
        }

        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }

    public static void renderParticle(WorldRenderer renderer, float time, float scale)
    {
        float f1 = ActiveRenderInfo.getRotationX();
        float f2 = ActiveRenderInfo.getRotationZ();
        float f3 = ActiveRenderInfo.getRotationYZ();
        float f4 = ActiveRenderInfo.getRotationXY();
        float f5 = ActiveRenderInfo.getRotationXZ();
//        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)time;
//        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)time;
//        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)time;

        float f6 = 0.0f;
        float f7 = 1.0f;
        float f8 = 0.0f;
        float f9 = 1.0f;
        float f10 = scale;

        float f11 = 0.0f;
        float f12 = 0.0f;
        float f13 = 0.0f;
        renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        renderer.pos((double) (f11 - f1 * f10 - f3 * f10), (double) (f12 - f5 * f10), (double) (f13 - f2 * f10 - f4 * f10)).tex((double) f7, (double) f9).endVertex();
        renderer.pos((double) (f11 - f1 * f10 + f3 * f10), (double) (f12 + f5 * f10), (double) (f13 - f2 * f10 + f4 * f10)).tex((double) f7, (double) f8).endVertex();
        renderer.pos((double) (f11 + f1 * f10 + f3 * f10), (double) (f12 + f5 * f10), (double) (f13 + f2 * f10 + f4 * f10)).tex((double) f6, (double) f8).endVertex();
        renderer.pos((double) (f11 + f1 * f10 - f3 * f10), (double) (f12 - f5 * f10), (double) (f13 + f2 * f10 - f4 * f10)).tex((double) f6, (double) f9).endVertex();
        Tessellator.getInstance().draw();
    }

    public static void drawNormalCube(WorldRenderer tessellator, float size, float in, boolean lined)
    {
        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        else
        {
            tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        tessellator.pos(-size * in, -size * in, -size).endVertex();
        tessellator.pos(size * in, -size * in, -size).endVertex();
        tessellator.pos(size * in, size * in, -size).endVertex();
        tessellator.pos(-size * in, size * in, -size).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-size * in, -size * in, size).endVertex();
        tessellator.pos(-size * in, size * in, size).endVertex();
        tessellator.pos(size * in, size * in, size).endVertex();
        tessellator.pos(size * in, -size * in, size).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-size, -size * in, -size * in).endVertex();
        tessellator.pos(-size, size * in, -size * in).endVertex();
        tessellator.pos(-size, size * in, size * in).endVertex();
        tessellator.pos(-size, -size * in, size * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(size, -size * in, -size * in).endVertex();
        tessellator.pos(size, -size * in, size * in).endVertex();
        tessellator.pos(size, size * in, size * in).endVertex();
        tessellator.pos(size, size * in, -size * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-size * in, size, -size * in).endVertex();
        tessellator.pos(size * in, size, -size * in).endVertex();
        tessellator.pos(size * in, size, size * in).endVertex();
        tessellator.pos(-size * in, size, size * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-size * in, -size, -size * in).endVertex();
        tessellator.pos(-size * in, -size, size * in).endVertex();
        tessellator.pos(size * in, -size, size * in).endVertex();
        tessellator.pos(size * in, -size, -size * in).endVertex();

        Tessellator.getInstance().draw();
    }

    public static void drawCuboid(WorldRenderer tessellator, float sizeX, float sizeY, float sizeZ, float in, boolean lined)
    {
        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        else
        {
            tessellator.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
        }

        tessellator.pos(-sizeX * in, -sizeY * in, -sizeZ).endVertex();
        tessellator.pos(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        tessellator.pos(sizeX * in, sizeY * in, -sizeZ).endVertex();
        tessellator.pos(sizeX * in, -sizeY * in, -sizeZ).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-sizeX * in, -sizeY * in, sizeZ).endVertex();
        tessellator.pos(sizeX * in, -sizeY * in, sizeZ).endVertex();
        tessellator.pos(sizeX * in, sizeY * in, sizeZ).endVertex();
        tessellator.pos(-sizeX * in, sizeY * in, sizeZ).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-sizeX, -sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(-sizeX, -sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(-sizeX, sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(-sizeX, sizeY * in, -sizeZ * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(sizeX, -sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(sizeX, sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(sizeX, sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(sizeX, -sizeY * in, sizeZ * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-sizeX * in, sizeY, -sizeZ * in).endVertex();
        tessellator.pos(-sizeX * in, sizeY, sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, sizeY, sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, sizeY, -sizeZ * in).endVertex();
        if (lined)
        {
            Tessellator.getInstance().draw();
        }

        if (lined)
        {
            tessellator.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_TEX_COLOR);
        }
        tessellator.pos(-sizeX * in, -sizeY, -sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, -sizeY, -sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, -sizeY, sizeZ * in).endVertex();
        tessellator.pos(-sizeX * in, -sizeY, sizeZ * in).endVertex();

        Tessellator.getInstance().draw();
    }

    public static void renderCuboid(WorldRenderer tessellator, float sizeX, float sizeY, float sizeZ, float in)
    {
        tessellator.pos(-sizeX * in, -sizeY * in, -sizeZ).endVertex();
        tessellator.pos(-sizeX * in, sizeY * in, -sizeZ).endVertex();
        tessellator.pos(sizeX * in, sizeY * in, -sizeZ).endVertex();
        tessellator.pos(sizeX * in, -sizeY * in, -sizeZ).endVertex();

        tessellator.pos(-sizeX * in, -sizeY * in, sizeZ).endVertex();
        tessellator.pos(sizeX * in, -sizeY * in, sizeZ).endVertex();
        tessellator.pos(sizeX * in, sizeY * in, sizeZ).endVertex();
        tessellator.pos(-sizeX * in, sizeY * in, sizeZ).endVertex();

        tessellator.pos(-sizeX, -sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(-sizeX, -sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(-sizeX, sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(-sizeX, sizeY * in, -sizeZ * in).endVertex();

        tessellator.pos(sizeX, -sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(sizeX, sizeY * in, -sizeZ * in).endVertex();
        tessellator.pos(sizeX, sizeY * in, sizeZ * in).endVertex();
        tessellator.pos(sizeX, -sizeY * in, sizeZ * in).endVertex();

        tessellator.pos(-sizeX * in, sizeY, -sizeZ * in).endVertex();
        tessellator.pos(-sizeX * in, sizeY, sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, sizeY, sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, sizeY, -sizeZ * in).endVertex();

        tessellator.pos(-sizeX * in, -sizeY, -sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, -sizeY, -sizeZ * in).endVertex();
        tessellator.pos(sizeX * in, -sizeY, sizeZ * in).endVertex();
        tessellator.pos(-sizeX * in, -sizeY, sizeZ * in).endVertex();
    }

    public static void drawModelCuboid(WorldRenderer renderer, float x, float y, float z, float sizeX, float sizeY, float sizeZ)
    {
        float tM = 1.0f / 16.0f;

        float transX = (x + sizeX * 0.5f) * tM;
        float transY = (y + sizeY * 0.5f) * tM - 0.5f;
        float transZ = (z + sizeZ * 0.5f) * tM;

        WorldRendererAccessor.addTranslation(renderer, transX, transY, transZ);
        renderCuboid(renderer, sizeX * tM * 0.5f, sizeY * tM * 0.5f, sizeZ * tM * 0.5f, 1.0f);
        WorldRendererAccessor.addTranslation(renderer, -transX, -transY, -transZ);
    }
}
