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

package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvMathHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 20.04.14.
 */
public class IvBezierPath3DRendererTexture
{
    private double lineWidth;
    private double stepSize;
    private double textureShift;

    public IvBezierPath3DRendererTexture()
    {
        lineWidth = 1.0;
        stepSize = 0.1;
    }

    public double getLineWidth()
    {
        return lineWidth;
    }

    public void setLineWidth(double lineWidth)
    {
        this.lineWidth = lineWidth;
    }

    public double getStepSize()
    {
        return stepSize;
    }

    public void setStepSize(double stepSize)
    {
        this.stepSize = stepSize;
    }

    public double getTextureShift()
    {
        return textureShift;
    }

    public void setTextureShift(double textureShift)
    {
        this.textureShift = textureShift;
    }

    public void render(IvBezierPath3D path)
    {
        if (path.isDirty())
        {
            path.buildDistances();
        }

        BufferBuilder renderer = Tessellator.getInstance().getBuffer();

        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        for (double progress = 0.0; progress < (1.0 + stepSize); progress += stepSize)
        {
            boolean isVeryFirst = progress == 0.0;
            boolean isVeryLast = progress >= 1.0;

            double totalProgress = Math.min(progress, 1.0);
            IvBezierPoint3DCachedStep cachedStep = path.getCachedStep(totalProgress);
            double[] position = cachedStep.getPosition();
            double[] pVector = path.getPVector(cachedStep, stepSize);

            double red = IvMathHelper.mix(cachedStep.getLeftPoint().getRed(), cachedStep.getRightPoint().getRed(), cachedStep.getInnerProgress());
            double green = IvMathHelper.mix(cachedStep.getLeftPoint().getGreen(), cachedStep.getRightPoint().getGreen(), cachedStep.getInnerProgress());
            double blue = IvMathHelper.mix(cachedStep.getLeftPoint().getBlue(), cachedStep.getRightPoint().getBlue(), cachedStep.getInnerProgress());
            double alpha = IvMathHelper.mix(cachedStep.getLeftPoint().getAlpha(), cachedStep.getRightPoint().getAlpha(), cachedStep.getInnerProgress());

            double textureX = totalProgress + textureShift;
            if (!isVeryFirst)
            {
                renderer.color((float) red, (float) green, (float) blue, (float) alpha);
                renderer.pos(position[0] - pVector[0] * lineWidth, position[1] - pVector[1] * lineWidth, position[2] - pVector[2] * lineWidth).tex(textureX, 0).endVertex();
                renderer.pos(position[0] + pVector[0] * lineWidth, position[1] + pVector[1] * lineWidth, position[2] + pVector[2] * lineWidth).tex(textureX, 1).endVertex();
                Tessellator.getInstance().draw();
            }

            if (!isVeryLast)
            {
                renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
                renderer.color((float) red, (float) green, (float) blue, (float) alpha);
                renderer.pos(position[0] + pVector[0] * lineWidth, position[1] + pVector[1] * lineWidth, position[2] + pVector[2] * lineWidth).tex(textureX, 1).endVertex();
                renderer.pos(position[0] - pVector[0] * lineWidth, position[1] - pVector[1] * lineWidth, position[2] - pVector[2] * lineWidth).tex(textureX, 0).endVertex();
            }
        }

        GlStateManager.disableBlend();
    }
}
