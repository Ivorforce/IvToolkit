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

import ivorius.ivtoolkit.math.IvVecMathHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeComponent extends WeightedRandom.Item
{
    private Object identifier;

    private List<MazeRoom> rooms = new ArrayList<>();
    private List<MazePath> exitPaths = new ArrayList<>();

    public MazeComponent(int par1, Object identifier, List<MazeRoom> rooms, List<MazePath> exitPaths)
    {
        super(par1);
        this.identifier = identifier;
        this.rooms.addAll(rooms);
        this.exitPaths.addAll(exitPaths);
    }

    public Object getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(Object identifier)
    {
        this.identifier = identifier;
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
}
