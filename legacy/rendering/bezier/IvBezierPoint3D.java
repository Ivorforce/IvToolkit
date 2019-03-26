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

public class IvBezierPoint3D
{
    public double[] position;
    public double[] bezierDirection;
    public double rotation;

    public int color;

    public double fontSize;

    public IvBezierPoint3D(double[] position, double[] bezierDirection, int color, double rotation)
    {
        this.position = position;

        this.bezierDirection = bezierDirection;

        this.color = color;

        this.rotation = rotation;

        this.fontSize = 1.0;
    }

    public IvBezierPoint3D(double[] position, double[] bezierDirection, int color, double rotation, double fontSize)
    {
        this(position, bezierDirection, color, rotation);

        this.fontSize = fontSize;
    }

    public double getAlpha()
    {
        return ((color >> 24) & 255) / 255.0;
    }

    public double getRed()
    {
        return ((color >> 16) & 255) / 255.0;
    }

    public double getGreen()
    {
        return ((color >> 8) & 255) / 255.0;
    }

    public double getBlue()
    {
        return (color & 255) / 255.0;
    }

    public double[] getBezierDirectionPointTo()
    {
        double[] result = new double[bezierDirection.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = position[i] - bezierDirection[i];
        }

        return result;
    }

    public double[] getBezierDirectionPointFrom()
    {
        double[] result = new double[bezierDirection.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = position[i] + bezierDirection[i];
        }

        return result;
    }

    public double getFontSize()
    {
        return fontSize;
    }
}
