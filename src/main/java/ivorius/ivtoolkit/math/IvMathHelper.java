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

package ivorius.ivtoolkit.math;

import net.minecraft.util.MathHelper;

import java.util.Random;

public class IvMathHelper
{
    public static double mix(double value1, double value2, double progress)
    {
        return value1 + (value2 - value1) * progress;
    }

    public static double mixEaseInOut(double value1, double value2, double progress)
    {
        return cubicMix(value1, value1, value2, value2, progress);
    }

    public static double easeZeroToOne(double progress)
    {
        return cubicMix(0.0, 0.0, 1.0, 1.0, clamp(0.0, progress, 1.0));
    }

    public static double quadraticMix(double value1, double value2, double value3, double progress)
    {
        return mix(mix(value1, value2, progress), mix(value2, value3, progress), progress);
    }

    public static double cubicMix(double value1, double value2, double value3, double value4, double progress)
    {
        return mix(quadraticMix(value1, value2, value3, progress), quadraticMix(value2, value3, value4, progress), progress);
    }

    public static float mix(float value1, float value2, float progress)
    {
        return value1 + (value2 - value1) * progress;
    }

    public static float mixEaseInOut(float value1, float value2, float progress)
    {
        return cubicMix(value1, value1, value2, value2, progress);
    }

    public static float easeZeroToOne(float progress)
    {
        return cubicMix(0.0f, 0.0f, 1.0f, 1.0f, clamp(0.0f, progress, 1.0f));
    }

    public static float quadraticMix(float value1, float value2, float value3, float progress)
    {
        return mix(mix(value1, value2, progress), mix(value2, value3, progress), progress);
    }

    public static float cubicMix(float value1, float value2, float value3, float value4, float progress)
    {
        return mix(quadraticMix(value1, value2, value3, progress), quadraticMix(value2, value3, value4, progress), progress);
    }

    public static float clamp(float min, float value, float max)
    {
        return value < min ? min : value > max ? max : value;
    }

    public static double clamp(double min, double value, double max)
    {
        return value < min ? min : value > max ? max : value;
    }

    public static float nearValue(float value, float dest, float mulSpeed, float plusSpeed)
    {
        value += (dest - value) * mulSpeed;

        if (value > dest)
        {
            value -= plusSpeed;
            if (value < dest)
                value = dest;
        }
        else if (value < dest)
        {
            value += plusSpeed;
            if (value > dest)
                value = dest;
        }

        return value;
    }

    public static double nearValue(double value, double dest, double mulSpeed, double plusSpeed)
    {
        value += (dest - value) * mulSpeed;

        if (value > dest)
        {
            value -= plusSpeed;
            if (value < dest)
                value = dest;
        }
        else if (value < dest)
        {
            value += plusSpeed;
            if (value > dest)
                value = dest;
        }

        return value;
    }

    public static int randomLinearNumber(Random random, float number)
    {
        return MathHelper.floor_float(number) + ((random.nextFloat() < (number % 1.0f)) ? 1 : 0);
    }

    public static float zeroToOne(float value, float min, float max)
    {
        return clamp(0.0f, (value - min) / (max - min), 1.0f);
    }

    public static double zeroToOne(double value, double min, double max)
    {
        return clamp(0.0, (value - min) / (max - min), 1.0);
    }
}
