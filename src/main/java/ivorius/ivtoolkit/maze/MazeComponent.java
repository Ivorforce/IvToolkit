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

package ivorius.ivtoolkit.maze;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import ivorius.ivtoolkit.tools.NBTTagCompounds;
import ivorius.ivtoolkit.tools.NBTTagLists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeComponent implements WeightedSelector.Item, NBTCompoundObject
{
    protected NBTTagCompound identifierCompound;
    protected NBTCompoundObject identifier;

    protected double weight;

    protected List<MazeRoom> rooms = new ArrayList<>();
    protected List<MazePath> exitPaths = new ArrayList<>();

    public MazeComponent()
    {

    }

    public MazeComponent(double weight, NBTCompoundObject identifier, List<MazeRoom> rooms, List<MazePath> exitPaths)
    {
        this.weight = weight;
        this.identifier = identifier;
        this.rooms.addAll(rooms);
        this.exitPaths.addAll(exitPaths);
    }

    public NBTCompoundObject getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(NBTCompoundObject identifier)
    {
        this.identifier = identifier;
    }

    public void setWeight(double weight)
    {
        this.weight = weight;
    }

    @Override
    public double getWeight()
    {
        return weight;
    }

    public List<MazeRoom> getRooms()
    {
        return Collections.unmodifiableList(rooms);
    }

    public void setRooms(List<MazeRoom> rooms)
    {
        this.rooms.clear();
        this.rooms.addAll(rooms);
    }

    public List<MazePath> getExitPaths()
    {
        return Collections.unmodifiableList(exitPaths);
    }

    public void setExitPaths(List<MazePath> exitPaths)
    {
        this.exitPaths.clear();
        this.exitPaths.addAll(exitPaths);
    }

    public int[] getSize()
    {
        int[] lowest = rooms.get(0).coordinates.clone();
        int[] highest = rooms.get(0).coordinates.clone();
        for (MazeRoom room : rooms)
        {
            for (int i = 0; i < room.coordinates.length; i++)
            {
                if (room.coordinates[i] < lowest[i])
                {
                    lowest[i] = room.coordinates[i];
                }
                else if (room.coordinates[i] > highest[i])
                {
                    highest[i] = room.coordinates[i];
                }
            }
        }

        int[] size = IvVecMathHelper.sub(highest, lowest);
        for (int i = 0; i < size.length; i++)
        {
            size[i]++;
        }

        return size;
    }

    @Override
    public void readFromNBT(final NBTTagCompound compound)
    {
        identifierCompound = compound.getCompoundTag("identifier");
        weight = compound.getDouble("weight");

        rooms.clear();
        rooms.addAll(Lists.transform(NBTTagLists.intArrays(compound, "rooms"), new Function<int[], MazeRoom>()
        {
            @Nullable
            @Override
            public MazeRoom apply(int[] input)
            {
                return new MazeRoom(input);
            }
        }));

        exitPaths.clear();
        exitPaths.addAll(Lists.transform(NBTTagLists.compounds(compound, "exitPaths"), new Function<NBTTagCompound, MazePath>()
        {
            @Nullable
            @Override
            public MazePath apply(@Nullable NBTTagCompound input)
            {
                return NBTTagCompounds.read(compound, MazePath.class);
            }
        }));
    }

    public void readIdentifier(Class<? extends NBTCompoundObject> iClass)
    {
        identifier = NBTTagCompounds.read(identifierCompound, iClass);
        identifierCompound = null;
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("identifier", NBTTagCompounds.write(identifier));
        compound.setDouble("weight", weight);

        compound.setTag("rooms", NBTTagLists.storeIntArrays(Lists.transform(rooms, new Function<MazeRoom, int[]>()
        {
            @Nullable
            @Override
            public int[] apply(@Nullable MazeRoom input)
            {
                return input.coordinates;
            }
        })));

        compound.setTag("exitPaths", NBTTagLists.storeCompounds(Lists.transform(exitPaths, new Function<MazePath, NBTTagCompound>()
        {
            @Nullable
            @Override
            public NBTTagCompound apply(@Nullable MazePath input)
            {
                return NBTTagCompounds.write(input);
            }
        })));
    }
}
