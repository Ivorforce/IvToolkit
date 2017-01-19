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
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBShaderObjects;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class IvOpenGLHelper
{
    public static int GL_VALIDATE_STATUS = GL20.GL_VALIDATE_STATUS;

    public static void glValidateProgram(int program)
    {
        Field useARBField = ReflectionHelper.findField(OpenGlHelper.class, "arbShaders", "field_153214_y");
        useARBField.setAccessible(true);
        boolean useARB = false;

        try
        {
            useARB = useARBField.getBoolean(null);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        if (useARB)
            ARBShaderObjects.glValidateProgramARB(program);
        else
            GL20.glValidateProgram(program);
    }

    public static int genStandardTexture()
    {
        int textureIndex = GlStateManager.generateTexture();

        GlStateManager.bindTexture(textureIndex);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

        return textureIndex;
    }

    public static void setUpOpenGLStandard2D(int screenWidth, int screenHeight)
    {
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, screenWidth, screenHeight, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.loadIdentity();
        GlStateManager.translate(0.0F, 0.0F, -2000.0F);
    }

    public static void checkGLError(Logger logger, String category)
    {
        int error;
        while ((error = GL11.glGetError()) != 0)
        {
            String s1 = GLU.gluErrorString(error);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + category);
            logger.error(error + ": " + s1);
        }
    }
}
