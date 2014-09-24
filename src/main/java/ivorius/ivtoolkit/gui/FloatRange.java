/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.gui;

/**
 * Created by lukas on 13.06.14.
 */
public class FloatRange
{
    public final float min;
    public final float max;

    public FloatRange(float min, float max)
    {
        this.min = min;
        this.max = max;
    }

    public FloatRange(IntegerRange integerRange)
    {
        min = integerRange.getMin();
        max = integerRange.getMax();
    }

    public float getMin()
    {
        return min;
    }

    public float getMax()
    {
        return max;
    }

    @Override
    public String toString()
    {
        return "FloatRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
