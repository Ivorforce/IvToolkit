/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.ivtoolkit.models.utils;

/**
 * Created by lukas on 22.06.14.
 */
public class VecMathUtils
{
    public static double lengthSQ(double[] vector)
    {
        double max = 0.0;

        for (double aValue : vector)
        {
            max += aValue * aValue;
        }

        return max;
    }

    public static double length(double[] vector)
    {
        return MathUtils.sqrt(lengthSQ(vector));
    }

    public static double distanceSQ(double[] pos1, double[] pos2)
    {
        double distanceSQ = 0.0;

        for (int i = 0; i < pos1.length; i++)
        {
            distanceSQ += (pos1[i] - pos2[i]) * (pos1[i] - pos2[i]);
        }

        return distanceSQ;
    }

    public static double distance(double[] pos1, double[] pos2)
    {
        return MathUtils.sqrt(distanceSQ(pos1, pos2));
    }

    public static double[] mix(double[] pos1, double[] pos2, double progress)
    {
        double[] result = new double[pos1.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = MathUtils.mix(pos1[i], pos2[i], progress);
        }

        return result;
    }

    public static double[] cubicMix(double[] pos1, double[] pos2, double[] pos3, double[] pos4, double progress)
    {
        double[] result = new double[pos1.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = MathUtils.cubicMix(pos1[i], pos2[i], pos3[i], pos4[i], progress);
        }

        return result;
    }

    public static double[] normalize(double[] vector)
    {
        double max = length(vector);

        double[] resultVector = new double[vector.length];
        for (int i = 0; i < vector.length; i++)
        {
            resultVector[i] = vector[i] / max;
        }

        return resultVector;
    }

    public static double dotProduct(double[] pos1, double[] pos2)
    {
        double dotProduct = 0.0;

        for (int i = 0; i < pos1.length; i++)
        {
            dotProduct += pos1[i] * pos2[i];
        }

        return dotProduct;
    }

    public static double[] perpendicularVector(double[] pos1, double[] premiseVector)
    {
        double[] resultVector = new double[pos1.length];
        double dotProduct = 0.0;

        for (int i = 0; i < pos1.length - 1; i++)
        {
            dotProduct += pos1[i] * premiseVector[i];
            resultVector[i] = premiseVector[i];
        }

        resultVector[pos1.length - 1] = -(dotProduct / pos1[pos1.length - 1]);

        return normalize(resultVector);
    }

    public static double[] crossProduct(double[] pos1, double[] pos2)
    {
        double[] resultVector = new double[pos1.length];

        resultVector[0] = pos1[1] * pos2[2] - pos1[2] * pos2[1];
        resultVector[1] = pos1[2] * pos2[0] - pos1[0] * pos2[2];
        resultVector[2] = pos1[0] * pos2[1] - pos1[1] * pos2[0];

        return normalize(resultVector);
    }

    /**
     * Calculates cartesian from spherical coordinates.
     *
     * @param vector The spherical vector {azimuth, inclination, radius}
     * @return The cartesian vector {x, y, z}
     */
    public static double[] cartesianFromSpherical(double[] vector)
    {
        double sinInclination = MathUtils.sin((float) vector[1]);

        double x = vector[2] * sinInclination * MathUtils.cos((float) vector[0]);
        double y = vector[2] * MathUtils.cos((float) vector[1]);
        double z = vector[2] * sinInclination * MathUtils.sin((float) vector[0]);

        return new double[]{x, y, z};
    }

    /**
     * Calculates spherical from cartesian coordinates.
     *
     * @param vector The cartesian vector {x, y, z}
     * @return The spherical vector {azimuth, inclination, radius}
     */
    public static double[] sphericalFromCartesian(double[] vector)
    {
        double r = length(vector);
        double inclination = Math.acos(vector[1] / r);
        double azimuth = Math.atan2(vector[2], vector[0]);

        return new double[]{azimuth, inclination, r};
    }

    public static int[] add(int[]... vectors)
    {
        int[] result = new int[vectors[0].length];
        for (int i = 0; i < result.length; i++)
        {
            for (int[] vector : vectors)
            {
                result[i] += vector[i];
            }
        }

        return result;
    }

    public static double[] add(double[]... vectors)
    {
        double[] result = new double[vectors[0].length];
        for (int i = 0; i < result.length; i++)
        {
            for (double[] vector : vectors)
            {
                result[i] += vector[i];
            }
        }

        return result;
    }

    public static int[] sub(int[] vector, int[]... subVectors)
    {
        int[] result = vector.clone();
        for (int i = 0; i < result.length; i++)
        {
            for (int[] subVector : subVectors)
            {
                result[i] -= subVector[i];
            }
        }

        return result;
    }

    public static double[] sub(double[] vector, double[]... subVectors)
    {
        double[] result = vector.clone();
        for (int i = 0; i < result.length; i++)
        {
            for (double[] subVector : subVectors)
            {
                result[i] -= subVector[i];
            }
        }

        return result;
    }
}