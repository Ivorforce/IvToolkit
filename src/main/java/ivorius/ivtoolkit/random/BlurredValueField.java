/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.random;

import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 12.10.14.
 */
public class BlurredValueField implements NBTCompoundObject
{
    private final List<Value> values = new ArrayList<>();

    private int[] size;

    public BlurredValueField()
    {
    }

    public BlurredValueField(int... size)
    {
        this.size = size;
    }

    public int[] getSize()
    {
        return size.clone();
    }

    public void addValue(Value value)
    {
        if (value.pos.length != size.length)
            throw new IllegalArgumentException();

        values.add(value);
    }

    public void addValue(double value, Random random)
    {
        int[] pos = new int[size.length];
        for (int i = 0; i < pos.length; i++)
            pos[i] = random.nextInt(size[i]);

        addValue(new Value(value, pos));
    }

    public double getValue(int... position)
    {
        double total = 0;
        double[] invDist = new double[values.size()];
        for (int i = 0; i < invDist.length; i++)
        {
            Value value = values.get(i);

            double dist = 0.0;

            // Get distance to point
            for (int j = 0; j < position.length; j++)
            {
                double d = position[j] - value.pos[j];
                dist += d * d;
            }

            // Extremify
            dist = dist * dist;
            dist = dist * dist;

            if (dist <= 0.0001)
                return value.value;

            invDist[i] = 1.0 / dist;
            total += invDist[i];
        }

        double retVal = 0;

        total = 1D / total; // Do this just once for performance

        for (int i = 0; i < invDist.length; i++)
             retVal += values.get(i).value * invDist[i] * total;

        return retVal;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        size = compound.getIntArray("size");
        values.addAll(NBTCompoundObjects.readListFrom(compound, "values", Value.class));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setIntArray("size", size);
        NBTCompoundObjects.writeListTo(compound, "values", values);
    }

    public static class Value implements NBTCompoundObject
    {
        private double value;
        private int[] pos;

        public Value()
        {
        }

        public Value(double value, int[] pos)
        {
            this.value = value;
            this.pos = pos;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            value = compound.getDouble("value");
            pos = compound.getIntArray("pos");
        }

        @Override
        public void writeToNBT(NBTTagCompound compound)
        {
            compound.setDouble("value", value);
            compound.setIntArray("pos", pos);
        }
    }
}
