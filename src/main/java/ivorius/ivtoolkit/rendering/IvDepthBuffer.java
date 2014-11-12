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
import org.lwjgl.util.glu.GLU;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;

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

            depthFB = OpenGlHelper.func_153165_e();
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, depthFB);

            OpenGlHelper.func_153188_a(OpenGlHelper.field_153198_e, OpenGlHelper.field_153201_h, GL_TEXTURE_2D, depthTextureIndex, 0);

            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);

            int status = OpenGlHelper.func_153167_i(OpenGlHelper.field_153198_e);
            if (status != OpenGlHelper.field_153202_i)
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
        int depthTextureIndex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureIndex);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT24, textureWidth, textureHeight, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);

        return depthTextureIndex;
    }

    public void deallocate()
    {
        setUp = false;

        if (depthTextureIndex > 0)
        {
            glDeleteTextures(depthTextureIndex);
            depthTextureIndex = 0;
        }
        if (depthFB > 0)
        {
            OpenGlHelper.func_153174_h(depthFB);
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
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, depthFB);
            glDrawBuffer(GL_NONE);
            glReadBuffer(GL_NONE);
        }
    }

    public void unbind()
    {
        if (isAllocated())
        {
            OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, 0);
            glDrawBuffer(GL_BACK);
            glReadBuffer(GL_BACK);

            if (parentFB > 0) // Binds buffers itself? Anyway, calling the draw and read buffer functions causes invalid operation
            {
                OpenGlHelper.func_153171_g(OpenGlHelper.field_153198_e, parentFB);
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
        glBindTexture(GL_TEXTURE_2D, textureIndex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_LUMINANCE);

        OpenGlHelper.setActiveTexture(glTexture);
        glBindTexture(GL_TEXTURE_2D, textureIndex);
        OpenGlHelper.setActiveTexture(ARBMultitexture.GL_TEXTURE0_ARB);
    }

    public static void bindTextureForDestination(int textureIndex)
    {
        glBindTexture(GL_TEXTURE_2D, textureIndex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
        glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
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
