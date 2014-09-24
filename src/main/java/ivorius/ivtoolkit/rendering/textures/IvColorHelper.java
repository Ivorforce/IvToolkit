/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering.textures;

import net.minecraft.util.MathHelper;

import java.awt.image.BufferedImage;

/**
 * Created by lukas on 26.07.14.
 */
public class IvColorHelper
{
    public static int[] getARBGInts(int argb)
    {
        return new int[]{argb >>> 24, (argb >>> 16) & 255, (argb >>> 8) & 255, argb & 255};
    }

    public static int[] getARBGInts(float[] argb)
    {
        return new int[]{MathHelper.floor_float(argb[0] * 255.0f + 0.5f), MathHelper.floor_float(argb[1] * 255.0f + 0.5f), MathHelper.floor_float(argb[2] * 255.0f + 0.5f), MathHelper.floor_float(argb[3] * 255.0f + 0.5f)};
    }

    public static float[] getARBGFloats(int[] argb)
    {
        return new float[]{(float) argb[0] / 255.0f, (float) argb[1] / 255.0f, (float) argb[2] / 255.0f, (float) argb[3] / 255.0f};
    }

    public static float[] getARBGFloats(int argb)
    {
        int alpha = argb >>> 24;
        int red = (argb >>> 16) & 255;
        int green = (argb >>> 8) & 255;
        int blue = argb & 255;

        return new float[]{(float) alpha / 255.0f, (float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f};
    }

    public static int getARBGInt(float[] argb)
    {
        int alpha = MathHelper.clamp_int(MathHelper.floor_float(argb[0] * 255.0f + 0.5f), 0, 255);
        int red = MathHelper.clamp_int(MathHelper.floor_float(argb[1] * 255.0f + 0.5f), 0, 255);
        int green = MathHelper.clamp_int(MathHelper.floor_float(argb[2] * 255.0f + 0.5f), 0, 255);
        int blue = MathHelper.clamp_int(MathHelper.floor_float(argb[3] * 255.0f + 0.5f), 0, 255);

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
