/*
 * Copyright 2015 Lukas Tenbrink
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

package ivorius.ivtoolkit.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.List;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTTagLists
{
    public static List<NBTTagCompound> compoundsFrom(NBTTagCompound compound, String key)
    {
        return compounds(compound.getTagList(key, Constants.NBT.TAG_COMPOUND));
    }

    public static List<NBTTagCompound> compounds(final NBTTagList list)
    {
        return Lists.transform(Ranges.rangeList(0, list.tagCount()), input -> list.getCompoundTagAt(input));
    }

    public static void writeCompoundsTo(NBTTagCompound compound, String key, List<NBTTagCompound> list)
    {
        compound.setTag(key, writeCompounds(list));
    }

    public static NBTTagList writeCompounds(List<NBTTagCompound> list)
    {
        NBTTagList tagList = new NBTTagList();
        list.forEach(tagList::appendTag);
        return tagList;
    }

    public static List<int[]> intArraysFrom(NBTTagCompound compound, String key)
    {
        return intArrays(compound.getTagList(key, Constants.NBT.TAG_INT_ARRAY));
    }

    public static List<int[]> intArrays(final NBTTagList list)
    {
        return Lists.transform(Ranges.rangeList(0, list.tagCount()), input -> list.func_150306_c(input));
    }

    public static void writeIntArraysTo(NBTTagCompound compound, String key, List<int[]> list)
    {
        compound.setTag(key, writeIntArrays(list));
    }

    public static NBTTagList writeIntArrays(List<int[]> list)
    {
        NBTTagList tagList = new NBTTagList();
        for (int[] array : list)
            tagList.appendTag(new NBTTagIntArray(array));
        return tagList;
    }

    public static List<NBTBase> nbtBases(NBTTagList nbt)
    {
        ImmutableList.Builder<NBTBase> list = new ImmutableList.Builder<>();
        NBTTagList copy = (NBTTagList) nbt.copy(); // TODO Change to getTagListAt when available

        while (copy.tagCount() > 0)
            list.add(copy.removeTag(0));

        return list.build();
    }

    public static List<NBTTagList> listsFrom(NBTTagCompound compound, String key)
    {
        return lists(compound.getTagList(key, Constants.NBT.TAG_LIST));
    }

    public static List<NBTTagList> lists(NBTTagList nbt)
    {
        if (nbt.func_150303_d() != Constants.NBT.TAG_LIST)
            throw new IllegalArgumentException();

        ImmutableList.Builder<NBTTagList> list = new ImmutableList.Builder<>();
        NBTTagList copy = (NBTTagList) nbt.copy(); // TODO Change to getTagListAt when available

        while (copy.tagCount() > 0)
            list.add((NBTTagList) copy.removeTag(0));

        return list.build();
    }

    public static void writeTo(NBTTagCompound compound, String key, List<NBTTagList> lists)
    {
        compound.setTag(key, write(lists));
    }

    public static NBTTagList write(List<NBTTagList> lists)
    {
        NBTTagList list = new NBTTagList();
        lists.forEach(list::appendTag);
        return list;
    }
}
