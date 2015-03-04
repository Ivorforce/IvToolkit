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
import ivorius.ivtoolkit.random.WeightedSelector;
import net.minecraft.util.WeightedRandom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeComponent extends WeightedRandom.Item implements WeightedSelector.Item
{
    protected Object identifier;
    protected double weight;

    protected List<MazeRoom> rooms = new ArrayList<>();
    protected List<MazePath> exitPaths = new ArrayList<>();

    @Deprecated
    public MazeComponent(int weight, Object identifier, List<MazeRoom> rooms, List<MazePath> exitPaths)
    {
        super(weight);
        this.identifier = identifier;
        this.weight = weight * 0.01;
        this.rooms.addAll(rooms);
        this.exitPaths.addAll(exitPaths);
    }

    public MazeComponent(double weight, Object identifier, List<MazeRoom> rooms, List<MazePath> exitPaths)
    {
        super((int) (weight * 100));
        this.weight = weight;
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
}
