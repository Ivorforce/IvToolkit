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
import ivorius.ivtoolkit.tools.NBTCompoundObject;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by lukas on 23.06.14.
 */
public class MazePath implements MazeCoordinate, Cloneable, NBTCompoundObject
{
    public MazeRoom sourceRoom;
    public int pathDimension;
    public boolean pathGoesUp;

    public MazePath()
    {
    }

    public MazePath(MazeRoom sourceRoom, int pathDimension, boolean pathGoesUp)
    {
        this.sourceRoom = sourceRoom;
        this.pathDimension = pathDimension;
        this.pathGoesUp = pathGoesUp;
    }

    public MazePath(int pathDimension, boolean pathGoesUp, int... roomCoordinates)
    {
        this(new MazeRoom(roomCoordinates), pathDimension, pathGoesUp);
    }

    public static MazePath pathFromSourceAndDest(MazeRoom source, MazeRoom dest)
    {
        for (int i = 0; i < source.coordinates.length; i++)
        {
            if (source.coordinates[i] != dest.coordinates[i])
                return new MazePath(source, i, source.coordinates[i] < dest.coordinates[i]);
        }

        return null;
    }

    public MazePath invertPath()
    {
        return new MazePath(getDestinationRoom(), pathDimension, !pathGoesUp);
    }

    public int[] getPathDirection()
    {
        int[] direction = new int[sourceRoom.coordinates.length];
        direction[pathDimension] = pathGoesUp ? 1 : -1;
        return direction;
    }

    public MazeRoom getSourceRoom()
    {
        return sourceRoom;
    }

    public MazeRoom getDestinationRoom()
    {
        return new MazeRoom(IvVecMathHelper.add(sourceRoom.getCoordinates(), getPathDirection()));
    }

    public MazePath add(MazeRoom room)
    {
        return new MazePath(sourceRoom.add(room), pathDimension, pathGoesUp);
    }

    public MazePath sub(MazeRoom room)
    {
        return new MazePath(sourceRoom.sub(room), pathDimension, pathGoesUp);
    }

    @Override
    protected MazePath clone()
    {
        return new MazePath(sourceRoom.clone(), pathDimension, pathGoesUp);
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

        MazePath mazePath = (MazePath) o;

        return pathDimension == mazePath.pathDimension
                && pathGoesUp == mazePath.pathGoesUp ? sourceRoom.equals(mazePath.sourceRoom) : invertPath().sourceRoom.equals(mazePath.sourceRoom);
    }

    @Override
    public int hashCode()
    {
        int result = sourceRoom.hashCode();
        result = 31 * result + pathDimension;
        result = 31 * result + (pathGoesUp ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return String.format("%s -> %sd%d", getSourceRoom(), pathGoesUp ? "+" : "-", pathDimension);
    }

    @Override
    public int[] getMazeCoordinates()
    {
        int[] coords = sourceRoom.getMazeCoordinates();
        coords[pathDimension] += pathGoesUp ? 1 : -1;
        return coords;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        sourceRoom = new MazeRoom(compound.getCompoundTag("source"));
        pathDimension = compound.getInteger("pathDimension");
        pathGoesUp = compound.getBoolean("pathGoesUp");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("source", sourceRoom.writeToNBT());
        compound.setInteger("pathDimension", pathDimension);
        compound.setBoolean("pathGoesUp", pathGoesUp);
    }
}
