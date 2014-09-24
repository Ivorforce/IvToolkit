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

import java.util.Arrays;

/**
 * Created by lukas on 23.06.14.
 */
public class MazeRoom implements MazeCoordinate, Cloneable
{
    public final int[] coordinates;

    public MazeRoom(int... coordinates)
    {
        this.coordinates = coordinates;
    }

    public MazeRoom(NBTTagCompound compound)
    {
        coordinates = compound.getIntArray("coordinates");
    }

    public int getDimensions()
    {
        return coordinates.length;
    }

    public int[] getCoordinates()
    {
        return coordinates;
    }

    public MazeRoom add(MazeRoom room)
    {
        return new MazeRoom(IvVecMathHelper.add(coordinates, room.coordinates));
    }

    public MazeRoom sub(MazeRoom room)
    {
        return new MazeRoom(IvVecMathHelper.sub(coordinates, room.coordinates));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        MazeRoom mazeRoom = (MazeRoom) o;

        if (!Arrays.equals(coordinates, mazeRoom.coordinates))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(coordinates);
    }

    @Override
    public String toString()
    {
        return Arrays.toString(coordinates);
    }

    @Override
    public MazeRoom clone()
    {
        return new MazeRoom(coordinates.clone());
    }

    @Override
    public int[] getMazeCoordinates()
    {
        int[] coords = new int[coordinates.length];
        for (int i = 0; i < coords.length; i++)
        {
            coords[i] = coordinates[i] * 2 + 1;
        }

        return coords;
    }

    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setIntArray("coordinates", coordinates);
        return compound;
    }
}
