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

import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBMultitexture;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.util.glu.GLU;

import java.nio.ByteBuffer;

import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * Created by lukas on 26.02.14.
 */
public class IvDepthBuffer
{
    public Logger logger;

    private boolean setUp;

    private int depthTextureIndex;
    private int depthFB;

    private int parentFB;

    private int textureWidth;
    private int textureHeight;

    public IvDepthBuffer(int width, int height, Logger logger)
    {
        setParentFB(0);
        setSize(width, height);

        this.logger = logger;
    }

    public boolean allocate()
    {
        deallocate();

        if (OpenGlHelper.framebufferSupported && textureWidth > 0 && textureHeight > 0)
        {
            depthTextureIndex = genDefaultDepthTexture(textureWidth, textureHeight);

            depthFB = OpenGlHelper.glGenFramebuffers();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, depthFB);

            OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTextureIndex, 0);

            GL11.glDrawBuffer(GL11.GL_NONE);
            GL11.glReadBuffer(GL11.GL_NONE);

            int status = OpenGlHelper.glCheckFramebufferStatus(OpenGlHelper.GL_FRAMEBUFFER);
            if (status != OpenGlHelper.GL_FRAMEBUFFER_COMPLETE)
            {
                logger.error("Depth FBO failed setting up! (" + getFramebufferStatusString(status) + ")");
            }
            else
            {
                setUp = true;
            }

            unbind();
        }

        return setUp;
    }

    public static int genDefaultDepthTexture(int textureWidth, int textureHeight)
    {
        int depthTextureIndex = GlStateManager.generateTexture();
        GlStateManager.bindTexture(depthTextureIndex);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_INTENSITY);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, textureWidth, textureHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);

        return depthTextureIndex;
    }

    public void deallocate()
    {
        setUp = false;

        if (depthTextureIndex > 0)
        {
            GlStateManager.deleteTexture(depthTextureIndex);
            depthTextureIndex = 0;
        }
        if (depthFB > 0)
        {
            OpenGlHelper.glDeleteFramebuffers(depthFB);
            depthFB = 0;
        }
    }

    public int getDepthFBObject()
    {
        return isAllocated() ? depthFB : 0;
    }

    public int getDepthTextureIndex()
    {
        return isAllocated() ? depthTextureIndex : 0;
    }

    public void bind()
    {
        if (isAllocated())
        {
            bindTextureForDestination();
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, depthFB);
            GL11.glDrawBuffer(GL11.GL_NONE);
            GL11.glReadBuffer(GL11.GL_NONE);
        }
    }

    public void unbind()
    {
        if (isAllocated())
        {
            OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, 0);
            GL11.glDrawBuffer(GL11.GL_BACK);
            GL11.glReadBuffer(GL11.GL_BACK);

            if (parentFB > 0) // Binds buffers itself? Anyway, calling the draw and read buffer functions causes invalid operation
            {
                OpenGlHelper.glBindFramebuffer(OpenGlHelper.GL_FRAMEBUFFER, parentFB);
            }
        }
    }

    public boolean isAllocated()
    {
        return setUp;
    }

    public void setSize(int width, int height)
    {
        if (textureWidth != width || textureHeight != height)
        {
            textureWidth = width;
            textureHeight = height;

            if (isAllocated())
            {
                allocate();
            }
        }
    }

    public int getParentFB()
    {
        return parentFB;
    }

    public void setParentFB(int parentFB)
    {
        this.parentFB = parentFB > 0 ? parentFB : 0;
    }

    public static void bindTextureForSource(int glTexture, int textureIndex)
    {
        GlStateManager.bindTexture(textureIndex);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_LUMINANCE);

        OpenGlHelper.setActiveTexture(glTexture);
        GlStateManager.bindTexture(textureIndex);
        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
    }

    public static void bindTextureForDestination(int textureIndex)
    {
        GlStateManager.bindTexture(textureIndex);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_INTENSITY);
    }

    public void bindTextureForSource(int glTexture)
    {
        bindTextureForSource(glTexture, getDepthTextureIndex());
    }

    public void bindTextureForDestination()
    {
        bindTextureForDestination(getDepthTextureIndex());
    }

    public int getTextureWidth()
    {
        return textureWidth;
    }

    public int getTextureHeight()
    {
        return textureHeight;
    }

    public static String getFramebufferStatusString(int code)
    {
        return code + ": " + GLU.gluErrorString(code);
    }
}
