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

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
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
}
