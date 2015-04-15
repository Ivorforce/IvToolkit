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

package ivorius.ivtoolkit.maze.classic;

import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

/**
 * Created by lukas on 23.06.14.
 */
public class MazePath implements MazeCoordinate
{
    @Nonnull
    private final MazeRoom sourceRoom;
    private final int pathDimension;

    private MazeRoom destRoomCache;

    public MazePath(int pathDimension, @Nonnull MazeRoom sourceRoom)
    {
        this.sourceRoom = sourceRoom;
        this.pathDimension = pathDimension;
    }

    public MazePath(int pathDimension, @Nonnull int... roomCoordinates)
    {
        this(pathDimension, new MazeRoom(roomCoordinates.clone()));
    }

    public MazePath(NBTTagCompound compound)
    {
        sourceRoom = new MazeRoom(compound.getIntArray("source"));
        pathDimension = compound.getInteger("pathDimension");
    }

    public static MazePath fromRoom(int pathDimension, MazeRoom sourceRoom, boolean pathGoesUp)
    {
        return new MazePath(pathDimension, pathGoesUp ? sourceRoom : sourceRoom.subInDimension(pathDimension, 1));
    }

    public static MazePath fromConnection(MazeRoom source, MazeRoom dest)
    {
        return fromConnection(source.getCoordinates(), dest.getCoordinates());
    }

    public static MazePath fromConnection(int[] source, int[] dest)
    {
        for (int i = 0; i < source.length; i++)
        {
            if (source[i] != dest[i])
                return new MazePath(i, source[i] < dest[i] ? source : dest);
        }

        return null;
    }

    public int getPathDimension()
    {
        return pathDimension;
    }

    public int[] getPathDirection()
    {
        int[] direction = new int[sourceRoom.getDimensions()];
        direction[pathDimension] = 1;
        return direction;
    }

    public MazeRoom getSourceRoom()
    {
        return sourceRoom;
    }

    public MazeRoom getDestinationRoom()
    {
        return destRoomCache != null
            ? destRoomCache
            : (destRoomCache = sourceRoom.addInDimension(pathDimension, 1));
    }

    public MazePath add(MazeRoom room)
    {
        return new MazePath(pathDimension, sourceRoom.add(room));
    }

    public MazePath sub(MazeRoom room)
    {
        return new MazePath(pathDimension, sourceRoom.sub(room));
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MazePath mazePath = (MazePath) o;

        return pathDimension == mazePath.pathDimension && sourceRoom.equals(mazePath.sourceRoom);
    }

    @Override
    public int hashCode()
    {
        int result = sourceRoom.hashCode();
        result = 31 * result + pathDimension;
        return result;
    }

    @Override
    public String toString()
    {
        return String.format("%s <-> %s", getSourceRoom(), getDestinationRoom());
    }

    @Override
    public int[] getMazeCoordinates()
    {
        int[] coords = sourceRoom.getMazeCoordinates();
        coords[pathDimension] += 1;
        return coords;
    }

    public NBTTagCompound storeInNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setIntArray("source", sourceRoom.getCoordinates());
        compound.setInteger("pathDimension", pathDimension);
        return compound;
    }
}
