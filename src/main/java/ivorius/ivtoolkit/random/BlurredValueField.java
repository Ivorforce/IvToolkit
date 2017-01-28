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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTCompoundObjects;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by lukas on 12.10.14.
 */
public class BlurredValueField implements NBTCompoundObject, BlurrablePivot
{
    public static final int MAX_VALUES_PER_CHUNK = 25;
    public static final int CHUNKS_SPLIT = 4;

    private List<Value> values = new ArrayList<>();

    private int[] chunkCount;
    private BlurredValueField[] chunks;

    private int[] offset;
    private int[] size;
    private int[] center; // TODO Use double[] for accuracy?
    private int[] neighborP1;
    private int[] neighborP2;

    private double weight;
    private double average;

    protected List<BlurrablePivot> asList = Collections.singletonList(this);

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

    public static double getValue(Iterable<? extends BlurrablePivot> values, int count, int[] position)
    {
        int idx = 0;

        double total = 0;
        double[] invDist = new double[count];
        for (BlurrablePivot value : values)
        {
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

            invDist[idx] = 1.0 / dist * value.weight();
            total += invDist[idx++];
        }

        double retVal = 0;

        total = 1D / total; // Do this just once for performance
        idx = 0;

        for (BlurrablePivot value : values)
        {
            retVal += value.value() * invDist[idx++] * total;
        }

        return retVal;
    }

    public boolean almostContains(int[] pos)
    {
        for (int d = 0; d < pos.length; d++)
        {
            // Values cached for performance (keep in mind this is called about few hundred million times
            if (pos[d] < neighborP1[d] || pos[d] >= neighborP2[d])
                return false;
        }

        return true;
    }

    protected void setBounds(int[] chunkOffset, int[] chunkSize)
    {
        offset = chunkOffset;
        size = chunkSize;

        center = new int[offset.length];
        for (int d = 0; d < offset.length; d++)
            center[d] = offset[d] + size[d] / 2;

        neighborP1 = new int[offset.length];
        neighborP2 = new int[offset.length];
        for (int d = 0; d < offset.length; d++)
        {
            neighborP1[d] = offset[d] - size[d];
            neighborP2[d] = offset[d] + size[d] * 2;
        }
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

        if (chunks != null)
        {
            if (!getChunk(value.pos).addValue(value))
                return false;
        }
        else
        {
            if (values.stream().anyMatch(v -> Arrays.equals(v.pos, value.pos)))
                return false;

            values.add(value);

            if (values.size() > MAX_VALUES_PER_CHUNK)
                split();
        }

        average = calculateAverage();
        weight += value.weight();

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
            return Arrays.stream(chunks).mapToDouble(BlurredValueField::weight).sum();

        return values.size();
    }

    protected int[] calculateCenter()
    {
        int[] center = new int[offset.length];
        for (int d = 0; d < offset.length; d++)
            center[d] = offset[d] + size[d] / 2;
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
        for (int d = 0; d < chunkPos.length; d++)
        {
            if (chunkPos[d] < 0 || chunkPos[d] >= chunkCount[d])
                return false;
        }
        return true;
    }

    protected int[] getChunkPos(int[] pos)
    {
        int[] chunkPos = new int[pos.length];
        for (int d = 0; d < pos.length; d++)
            chunkPos[d] = (pos[d] - offset[d]) * chunkCount[d] / (size[d]);
        return chunkPos;
    }

    private int chunkToIndex(int[] chunkPos)
    {
        int index = 0;

        for (int d = 0; d < chunkPos.length; d++)
        {
            index *= chunkCount[d];
            index += chunkPos[d];
        }
        return index;
    }

    protected int[] chunkFromIndex(int index)
    {
        int[] chunkPos = new int[size.length];
        for (int i = chunkPos.length - 1; i >= 0; i--)
        {
            chunkPos[i] = index % chunkCount[i];
            index /= chunkCount[i];
        }
        return chunkPos;
    }

    protected void split()
    {
        int max = 1;
        chunkCount = new int[size.length];

        for (int d = 0; d < size.length; d++)
        {
            // If size < split, splitting further makes no sense anyway
            chunkCount[d] = Math.min(size[d], CHUNKS_SPLIT);
            max *= chunkCount[d];
        }

        if (max > 1)
        {
            chunks = new BlurredValueField[max];

            for (int c = 0; c < chunks.length; c++)
            {
                int[] chunkPos = chunkFromIndex(c);

                int[] chunkOffset = new int[size.length];
                int[] chunkSize = new int[size.length];

                for (int d = 0; d < size.length; d++)
                {
                    chunkOffset[d] = calculateChunkOffset(d, chunkPos[d]);
                    int nextOffset = calculateChunkOffset(d, chunkPos[d] + 1);
                    chunkSize[d] = nextOffset - chunkOffset[d];
                }

                chunks[c] = new BlurredValueField(chunkOffset, chunkSize);
            }
        }

        for (Value value : values)
            getChunk(value.pos).addValue(value);
        values.clear();

    }

    private int calculateChunkOffset(int d, int chunkPos)
    {
        // Because getChunkPos rounds chunk pos down, on rounding errors the
        // lowest chunks get the biggest space -> we need to round offset up
        int roundUpCorrection = chunkCount[d] - 1;
        return offset[d] + ((size[d] * chunkPos) + roundUpCorrection) / chunkCount[d];
    }

    public boolean addValue(double value, Random random)
    {
        int[] pos = new int[size.length];
        for (int i = 0; i < pos.length; i++)
            pos[i] = random.nextInt(size[i]) + offset[i];

        return addValue(new Value(value, pos));
    }

    public double getValue(int... position)
    {
        List<List<? extends BlurrablePivot>> relevant = new ArrayList<>();
        int[] total = new int[1];
        addRelevantValues(position, relevant, total);
        return getValue(Iterables.concat(relevant), total[0], position);
    }

    protected void addRelevantValues(int[] position, List<List<? extends BlurrablePivot>> rv, int[] total)
    {
        if (chunks != null)
        {
            for (BlurredValueField chunk : chunks)
            {
                if (chunk.almostContains(position))
                    chunk.addRelevantValues(position, rv, total);
                else
                {
                    rv.add(chunk.asList);
                    total[0]++;
                }
            }
        }
        else
        {
            rv.add(values);
            total[0] += values.size();
        }
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
            chunkCount = compound.getIntArray("chunkCount");
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
            compound.setIntArray("chunkCount", chunkCount);
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
        @Nullable
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
