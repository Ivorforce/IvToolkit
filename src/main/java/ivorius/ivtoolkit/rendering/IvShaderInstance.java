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

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Matrix;
import org.lwjgl.util.vector.Matrix2f;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class IvShaderInstance
{
    public Logger logger;

    private int shaderID = 0;

    private boolean shaderActive = false;

    private TObjectIntMap<String> uniformLocations = new TObjectIntHashMap<>();

    public int getShaderID()
    {
        return shaderID;
    }

    public IvShaderInstance(Logger logger)
    {
        this.logger = logger;
    }

    public void trySettingUpShader(String vertexShaderFile, String fragmentShaderFile)
    {
        if (shaderID <= 0)
        {
            registerShader(vertexShaderFile, fragmentShaderFile);
        }
    }

    public void registerShader(String vertexShaderCode, String fragmentShaderCode)
    {
        deleteShader();

        int vertShader = -1;
        int fragShader = -1;

        try
        {
            if (vertexShaderCode != null)
                vertShader = createShader(vertexShaderCode, OpenGlHelper.field_153209_q);

            if (fragmentShaderCode != null)
                fragShader = createShader(fragmentShaderCode, OpenGlHelper.field_153210_r);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            return;
        }

        shaderID = OpenGlHelper.func_153183_d();

        if (vertShader > 0)
        {
            OpenGlHelper.func_153178_b(shaderID, vertShader);
            OpenGlHelper.func_153180_a(vertShader);
        }

        if (fragShader > 0)
        {
            OpenGlHelper.func_153178_b(shaderID, fragShader);
            OpenGlHelper.func_153180_a(fragShader);
        }

        OpenGlHelper.func_153179_f(shaderID);
        if (OpenGlHelper.func_153175_a(shaderID, OpenGlHelper.field_153207_o) == GL11.GL_FALSE)
            logger.error(OpenGlHelper.func_153166_e(shaderID, 0x8000));

        IvOpenGLHelper.glValidateProgram(shaderID);
        if (OpenGlHelper.func_153175_a(shaderID, IvOpenGLHelper.GL_VALIDATE_STATUS) == GL11.GL_FALSE)
            logger.error(OpenGlHelper.func_153166_e(shaderID, 0x8000));
    }

    private int createShader(String shaderCode, int shaderType) throws Exception
    {
        int shader = 0;
        try
        {
            shader = OpenGlHelper.func_153195_b(shaderType);

            if (shader == 0)
                return 0;

            byte[] shaderCodeBytes = shaderCode.getBytes();
            ByteBuffer shaderCodeBuf = BufferUtils.createByteBuffer(shaderCodeBytes.length);
            shaderCodeBuf.put(shaderCodeBytes);
            shaderCodeBuf.position(0);

            OpenGlHelper.func_153169_a(shader, shaderCodeBuf);
            OpenGlHelper.func_153170_c(shader);

            if (OpenGlHelper.func_153157_c(shader, OpenGlHelper.field_153208_p) == GL11.GL_FALSE)
            {
                throw new RuntimeException("Error creating shader: " + OpenGlHelper.func_153166_e(shader, 0x8000));
            }

            return shader;
        }
        catch (Exception exc)
        {
            if (shader != 0)
                OpenGlHelper.func_153180_a(shader);

            throw new RuntimeException(exc);
        }
    }

    public boolean useShader()
    {
        if (shaderID <= 0 && !shaderActive)
        {
            return false;
        }

        shaderActive = true;
        OpenGlHelper.func_153161_d(shaderID);

        return true;
    }

    public void stopUsingShader()
    {
        if (shaderID <= 0 && shaderActive)
        {
            return;
        }

        OpenGlHelper.func_153161_d(0);
        shaderActive = false;
    }

    public boolean isShaderActive()
    {
        return shaderActive;
    }

    public boolean setUniformInts(String key, int... ints)
    {
        return setUniformIntsOfType(key, ints.length, ints);
    }

    public boolean setUniformIntsOfType(String key, int typeLength, int... ints)
    {
        if (shaderID <= 0 || !shaderActive)
        {
            return false;
        }

        IntBuffer intBuffer = BufferUtils.createIntBuffer(ints.length);
        intBuffer.put(ints);
        intBuffer.position(0);

        switch (typeLength)
        {
            case 1:
                OpenGlHelper.func_153181_a(getUniformLocation(key), intBuffer);
                break;
            case 2:
                OpenGlHelper.func_153182_b(getUniformLocation(key), intBuffer);
                break;
            case 3:
                OpenGlHelper.func_153192_c(getUniformLocation(key), intBuffer);
                break;
            case 4:
                OpenGlHelper.func_153162_d(getUniformLocation(key), intBuffer);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return true;
    }

    public boolean setUniformFloats(String key, float... floats)
    {
        return setUniformFloatsOfType(key, floats.length, floats);
    }

    public boolean setUniformFloatsOfType(String key, int typeLength, float... floats)
    {
        if (shaderID <= 0 || !shaderActive)
        {
            return false;
        }

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(floats.length);
        floatBuffer.put(floats);
        floatBuffer.position(0);

        switch (typeLength)
        {
            case 1:
                OpenGlHelper.func_153168_a(getUniformLocation(key), floatBuffer);
                break;
            case 2:
                OpenGlHelper.func_153177_b(getUniformLocation(key), floatBuffer);
                break;
            case 3:
                OpenGlHelper.func_153191_c(getUniformLocation(key), floatBuffer);
                break;
            case 4:
                OpenGlHelper.func_153159_d(getUniformLocation(key), floatBuffer);
                break;
            default:
                throw new IllegalArgumentException();
        }

        return true;
    }

    public boolean setUniformMatrix(String key, Matrix matrix)
    {
        if (shaderID <= 0 || !shaderActive)
        {
            return false;
        }

        int width;
        if (matrix instanceof Matrix2f)
            width = 2;
        else if (matrix instanceof Matrix3f)
            width = 3;
        else if (matrix instanceof Matrix4f)
            width = 4;
        else
            throw new IllegalArgumentException();

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(width * width);
        matrix.store(floatBuffer);
        floatBuffer.position(0);

        switch (width)
        {
            case 2:
                OpenGlHelper.func_153173_a(getUniformLocation(key), false, floatBuffer);
                break;
            case 3:
                OpenGlHelper.func_153189_b(getUniformLocation(key), false, floatBuffer);
                break;
            default:
                OpenGlHelper.func_153160_c(getUniformLocation(key), false, floatBuffer);
                break;
        }

        return true;
    }

    public Integer getUniformLocation(String key)
    {
        if (shaderID <= 0)
            return 0;

        if (!uniformLocations.containsKey(key))
            uniformLocations.put(key, OpenGlHelper.func_153194_a(shaderID, key));

        return uniformLocations.get(key);
    }

    public void deleteShader()
    {
        if (shaderActive)
            stopUsingShader();

        if (shaderID > 0)
        {
            OpenGlHelper.func_153187_e(shaderID);
            shaderID = 0;
        }

        uniformLocations.clear();
    }

    public static void outputShaderInfo(Logger logger)
    {
        String renderer = GL11.glGetString(GL11.GL_RENDERER);
        String vendor = GL11.glGetString(GL11.GL_VENDOR);
        String version = GL11.glGetString(GL11.GL_VERSION);
        boolean fboSupported = OpenGlHelper.framebufferSupported;

        String majorVersion;
        String minorVersion;

        String glslVersion;

        try
        {
            glslVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
        }
        catch (Exception ex)
        {
            glslVersion = "? (No GL20)";
        }

        try
        {
            minorVersion = "" + GL11.glGetInteger(GL30.GL_MINOR_VERSION);
            majorVersion = "" + GL11.glGetInteger(GL30.GL_MAJOR_VERSION);
        }
        catch (Exception ex)
        {
            minorVersion = "?";
            majorVersion = "? (No GL 30)";
        }

        printAlignedInfo("Vendor", vendor, logger);
        printAlignedInfo("Renderer", renderer, logger);
        printAlignedInfo("Version", version, logger);
        printAlignedInfo("Versions", getGLVersions(GLContext.getCapabilities()), logger);
        printAlignedInfo("Version Range", String.format("%s - %s", minorVersion, majorVersion), logger);
        printAlignedInfo("GLSL Version", glslVersion, logger);
        printAlignedInfo("Frame buffer object", fboSupported ? "Supported" : "Unsupported", logger);
    }

    private static void printAlignedInfo(String category, String info, Logger logger)
    {
        logger.info(String.format("%-20s: %s", category, info));
    }

    private static String getGLVersions(ContextCapabilities cap)
    {
        String versions = "";

        try
        {
            if (cap.OpenGL11)
                versions += ":11";
            if (cap.OpenGL12)
                versions += ":12";
            if (cap.OpenGL13)
                versions += ":13";
            if (cap.OpenGL14)
                versions += ":14";
            if (cap.OpenGL15)
                versions += ":15";
        }
        catch (Throwable throwable)
        {
            versions += ":lwjgl-Error-1";
        }

        try
        {
            if (cap.OpenGL20)
                versions += ":20";
            if (cap.OpenGL21)
                versions += ":21";
        }
        catch (Throwable throwable)
        {
            versions += ":lwjgl-Error-2";
        }

        try
        {
            if (cap.OpenGL30)
                versions += ":30";
            if (cap.OpenGL31)
                versions += ":31";
            if (cap.OpenGL32)
                versions += ":32";
            if (cap.OpenGL33)
                versions += ":33";
        }
        catch (Throwable throwable)
        {
            versions += ":lwjgl-Error-3";
        }

//        try
//        {
//            if (cap.OpenGL40)
//            {
//                versions += ":40";
//            }
//            if (cap.OpenGL41)
//            {
//                versions += ":41";
//            }
//            if (cap.OpenGL42)
//            {
//                versions += ":42";
//            }
//            if (cap.OpenGL43)
//            {
//                versions += ":43";
//            }
//            if (cap.OpenGL44)
//            {
//                versions += ":44";
//            }
//        }
//        catch (Throwable throwable)
//        {
//            versions += ":lwjgl-Error-4";
//        }

        if (versions.length() > 0)
            versions = versions.substring(1);

        return versions;
    }
}
