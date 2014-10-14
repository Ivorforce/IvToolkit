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

package ivorius.ivtoolkit.gui;

import net.minecraft.util.MathHelper;

/**
 * Created by lukas on 13.06.14.
 */
public class IntegerRange
{
    public final int min;
    public final int max;

    public IntegerRange(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    @Deprecated
    public IntegerRange(FloatRange floatRange)
    {
        min = MathHelper.floor_float(floatRange.getMin());
        max = MathHelper.floor_float(floatRange.getMax());
    }

    public int getMin()
    {
        return min;
    }

    public int getMax()
    {
        return max;
    }

    @Override
    public String toString()
    {
        return "IntegerRange{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}
