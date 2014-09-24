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
