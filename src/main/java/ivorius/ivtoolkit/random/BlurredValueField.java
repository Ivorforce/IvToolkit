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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by lukas on 12.10.14.
 */
public class BlurredValueField implements NBTCompoundObject, BlurrablePivot
{
    public static final int MAX_VALUES_PER_CHUNK = 100;
    public static final int CHUNKS_SPLIT = 3;

    private List<Value> values = new ArrayList<>();

    private int[] chunksSize;
    private BlurredValueField[] chunks;

    private int[] offset;
    private int[] size;
    private int[] center; // TODO Use double[] for accuracy?

    private double weight;
    private double average;

    public BlurredValueField()
    {
    }

    public BlurredValueField(int... size)
    {
        setBounds(new int[size.length], size);
    }

    public BlurredValueField(int[] offset, int[] size)
    {
        setBounds(offset, size);
    }

    protected void setBounds(int[] chunkOffset, int[] chunkSize)
    {
        offset = chunkOffset;
        size = chunkSize;
        center = calculateCenter();
    }

    public int[] getSize()
    {
        return size.clone();
    }

    public int[] getOffset()
    {
        return offset.clone();
    }

    public boolean addValue(Value value)
    {
        if (value.pos.length != size.length)
            throw new IllegalArgumentException();
        else if (values.stream().anyMatch(v -> Arrays.equals(v.pos, value.pos)))
            return false;

        if (chunks != null)
            getChunk(value.pos).addValue(value);
        else
        {
            values.add(value);

            if (values.size() > MAX_VALUES_PER_CHUNK)
                split();
        }

        average = calculateAverage();
        weight += value.weight;

        return true;
    }

    protected double calculateAverage()
    {
        if (chunks != null)
            return Arrays.stream(chunks).mapToDouble(c -> c.average).sum()
                    / chunks.length;

        return values.stream().mapToDouble(c -> c.value).sum() / values.size();
    }

    protected double calculateWeight()
    {
        if (chunks != null)
            return Arrays.stream(chunks).mapToDouble(c -> c.weight).sum();

        return values.size();
    }

    protected int[] calculateCenter()
    {
        int[] center = new int[offset.length];
        for (int i = 0; i < offset.length; i++)
            center[i] = offset[i] + size[i] / 2;
        return center;
    }

    protected BlurredValueField getChunk(int[] pos)
    {
        int[] chunkPos = getChunkPos(pos);

        if (!hasChunk(chunkPos))
            return null;

        return chunks[chunkToIndex(chunkPos)];
    }

    private boolean hasChunk(int[] chunkPos)
    {
        for (int i = 0; i < chunkPos.length; i++)
        {
            if (chunkPos[i] < 0 || chunkPos[i] >= chunksSize[i])
                return false;
        }
        return true;
    }

    protected int[] getChunkPos(int[] pos)
    {
        int[] chunkPos = new int[pos.length];
        for (int i = 0; i < pos.length; i++)
            chunkPos[i] = (pos[i] - offset[i]) / chunksSize[i];
        return chunkPos;
    }

    private int chunkToIndex(int[] chunkPos)
    {
        int index = 0;

        for (int i = 0; i < chunkPos.length; i++)
        {
            index *= chunksSize[i];
            index += chunkPos[i];
        }
        return index;
    }

    protected int[] chunkFromIndex(int index)
    {
        int[] chunkPos = new int[size.length];
        for (int i = chunkPos.length - 1; i >= 0; i--)
        {
            chunkPos[i] = index % chunksSize[i];
            index /= chunksSize[i];
        }
        return chunkPos;
    }

    protected void split()
    {
        int max = 1;
        chunksSize = new int[size.length];

        for (int i = 0; i < size.length; i++)
        {
            // If size < split, splitting further makes no sense anyway
            chunksSize[i] = Math.min(size[i], CHUNKS_SPLIT);
            max *= chunksSize[i];
        }

        if (max > 1)
        {
            chunks = new BlurredValueField[max];

            for (int c = 0; c < chunks.length; c++)
            {
                int[] chunkPos = chunkFromIndex(c);

                int[] chunkOffset = new int[size.length];
                int[] chunkSize = new int[size.length];

                for (int i = 0; i < size.length; i++)
                {
                    chunkOffset[i] = offset[i] + (size[i] * chunkPos[i]) / chunksSize[i];
                    int nextOffset = offset[i] + (size[i] * (chunkPos[i] + 1)) / chunksSize[i];
                    chunkSize[i] = nextOffset - chunkOffset[i];
                }

                chunks[c] = new BlurredValueField(chunkOffset, chunkSize);
            }
        }

        for (Value value : values)
            getChunk(value.pos).addValue(value);
        values.clear();

    }

    public boolean addValue(double value, Random random)
    {
        int[] pos = new int[size.length];
        for (int i = 0; i < pos.length; i++)
            pos[i] = random.nextInt(size[i]) + offset[i];

        return addValue(new Value(value, pos));
    }

    public boolean almostContains(BlurredValueField bounds, int[] pos)
    {
        for (int i = 0; i < pos.length; i++)
        {
            if (bounds.offset[i] - bounds.size[i] > pos[i]
                    || bounds.offset[i] + bounds.size[i] * 2 <= pos[i])
                return false;
        }

        return true;
    }

    public double getValue(int... position)
    {
        if (chunks != null)
        {
            int[] chunkPos = getChunkPos(position);

            // Only calculate for close ones, use average of distant ones
            return getValue(Arrays.stream(this.chunks).map(chunk -> almostContains(chunk, chunkPos)
                    ? new Value(chunk.getValue(position), chunk.weight(), chunk.pos()) : chunk)
                    .collect(Collectors.toList()), position);
        }

        return getValue(this.values, position);
    }

    public double getValue(List<? extends BlurrablePivot> values, int[] position)
    {
        double total = 0;
        double[] invDist = new double[values.size()];
        for (int i = 0; i < invDist.length; i++)
        {
            BlurrablePivot value = values.get(i);

            double dist = 0.0;

            // Get distance to point
            for (int j = 0; j < position.length; j++)
            {
                double d = position[j] - value.pos()[j];
                dist += d * d;
            }

            // Extremify
            dist = dist * dist;
            dist = dist * dist;

            if (dist <= 0.0001)
                return value.value();

            invDist[i] = 1.0 / dist * value.weight();
            total += invDist[i];
        }

        double retVal = 0;

        total = 1D / total; // Do this just once for performance

        for (int i = 0; i < invDist.length; i++)
        {
            retVal += values.get(i).value() * invDist[i] * total;
        }

        return retVal;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        size = compound.getIntArray("size");
        offset = compound.getIntArray("offset");
        values.addAll(NBTCompoundObjects.readListFrom(compound, "values", Value.class));

        if (compound.hasKey("chunks"))
        {
            chunks = (BlurredValueField[]) NBTCompoundObjects.readListFrom(compound, "chunks", BlurredValueField::new).toArray();
            chunksSize = compound.getIntArray("chunksSize");
        }

        weight = calculateWeight();
        average = calculateAverage();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setIntArray("size", size);
        compound.setIntArray("offset", offset);
        NBTCompoundObjects.writeListTo(compound, "values", values);

        if (chunks != null)
        {
            NBTCompoundObjects.writeListTo(compound, "chunks", values);
            compound.setIntArray("chunksSize", chunksSize);
        }
    }

    @Override
    public double value()
    {
        return average;
    }

    @Override
    public double weight()
    {
        return weight;
    }

    @Override
    public int[] pos()
    {
        return center;
    }

    public static class Value implements NBTCompoundObject, BlurrablePivot
    {
        private double value;
        private Double weight;
        private int[] pos;

        public Value()
        {
        }

        public Value(double value, int[] pos)
        {
            this.value = value;
            this.pos = pos;
        }

        public Value(double value, Double weight, int[] pos)
        {
            this.value = value;
            this.weight = weight;
            this.pos = pos;
        }

        public double getActiveWeight()
        {
            return weight != null ? weight : 1;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            value = compound.getDouble("value");
            weight = compound.hasKey("weight") ? compound.getDouble("weight") : null;
            pos = compound.getIntArray("pos");
        }

        @Override
        public void writeToNBT(NBTTagCompound compound)
        {
            compound.setDouble("value", value);
            if (weight != null)
                compound.setDouble("weight", weight);
            compound.setIntArray("pos", pos);
        }

        @Override
        public double value()
        {
            return value;
        }

        @Override
        public double weight()
        {
            return weight != null ? weight : 1;
        }

        @Override
        public int[] pos()
        {
            return pos;
        }
    }
}
