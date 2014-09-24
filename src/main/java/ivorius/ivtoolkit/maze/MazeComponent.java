/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
    private String identifier;

    private List<MazeRoom> rooms = new ArrayList<>();
    private List<MazePath> exitPaths = new ArrayList<>();

    public MazeComponent(int par1, String identifier, List<MazeRoom> rooms, List<MazePath> exitPaths)
    {
        super(par1);
        this.identifier = identifier;
        this.rooms.addAll(rooms);
        this.exitPaths.addAll(exitPaths);
    }

    public MazeComponent(NBTTagCompound compound)
    {
        super(compound.getInteger("weight"));

        identifier = compound.getString("identifier");

        NBTTagList roomsList = compound.getTagList("rooms", Constants.NBT.TAG_COMPOUND);
        rooms.clear();
        for (int i = 0; i < roomsList.tagCount(); i++)
        {
            rooms.add(new MazeRoom(roomsList.getCompoundTagAt(i)));
        }

        NBTTagList exitsList = compound.getTagList("exits", Constants.NBT.TAG_COMPOUND);
        exitPaths.clear();
        for (int i = 0; i < exitsList.tagCount(); i++)
        {
            exitPaths.add(new MazePath(exitsList.getCompoundTagAt(i)));
        }
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public void setIdentifier(String identifier)
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

    public void writeToNBT(NBTTagCompound compound)
    {
        compound.setString("identifier", identifier);
        compound.setInteger("weight", itemWeight);

        NBTTagList roomsList = new NBTTagList();
        for (MazeRoom room : rooms)
        {
            roomsList.appendTag(room.writeToNBT());
        }
        compound.setTag("rooms", roomsList);

        NBTTagList exitsList = new NBTTagList();
        for (MazePath exit : exitPaths)
        {
            exitsList.appendTag(exit.writeToNBT());
        }
        compound.setTag("exits", exitsList);
    }
}
