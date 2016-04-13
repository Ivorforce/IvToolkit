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
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTCompoundObjects
{
    @Deprecated
    public static <T extends NBTCompoundObject> Function<NBTTagCompound, T> readFunction(final Class<T> tClass)
    {
        return input -> read(input, tClass);
    }

    @Deprecated
    public static Function<? extends NBTCompoundObject, NBTTagCompound> writeFunction()
    {
        return (Function<NBTCompoundObject, NBTTagCompound>) NBTCompoundObjects::write;
    }

    public static void writeTo(NBTTagCompound compound, String key, NBTCompoundObject compoundObject)
    {
        compound.setTag(key, NBTCompoundObjects.write(compoundObject));
    }

    public static NBTTagCompound write(NBTCompoundObject compoundObject)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compoundObject.writeToNBT(compound);
        return compound;
    }

    public static <K extends NBTCompoundObject> K readFrom(NBTTagCompound compound, String key, Class<? extends K> keyClass)
    {
        return NBTCompoundObjects.read(compound.getCompoundTag(key), keyClass);
    }

    public static <T extends NBTCompoundObject> T read(NBTTagCompound compound, Class<T> tClass)
    {
        try
        {
            T t = tClass.newInstance();
            t.readFromNBT(compound);
            return t;
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public static void writeListTo(NBTTagCompound compound, String key, Iterable<? extends NBTCompoundObject> objects)
    {
        compound.setTag(key, writeList(objects));
    }

    public static NBTTagList writeList(Iterable<? extends NBTCompoundObject> objects)
    {
        NBTTagList tagList = new NBTTagList();
        for (NBTCompoundObject object : objects)
        {
            NBTTagCompound compound = new NBTTagCompound();
            object.writeToNBT(compound);
            tagList.appendTag(compound);
        }
        return tagList;
    }

    public static <T extends NBTCompoundObject> List<T> readListFrom(NBTTagCompound compound, String key, Class<T> tClass)
    {
        return readList(compound.getTagList(key, Constants.NBT.TAG_COMPOUND), tClass);
    }

    public static <T extends NBTCompoundObject> List<T> readList(NBTTagList list, Class<T> tClass)
    {
        List<T> rList = new ArrayList<>(list.tagCount());

        for (int i = 0; i < list.tagCount(); i++)
            rList.add(read(list.getCompoundTagAt(i), tClass));

        return rList;
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMapFrom(NBTTagCompound compound, String key, Class<? extends K> keyClass, Class<? extends V> valueClass)
    {
        return readMap(compound.getTagList(key, Constants.NBT.TAG_COMPOUND), keyClass, valueClass);
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> Map<K, V> readMap(NBTTagList nbt, Class<? extends K> keyClass, Class<? extends V> valueClass)
    {
        ImmutableMap.Builder<K, V> map = new ImmutableMap.Builder<>();
        for (int i = 0; i < nbt.tagCount(); i++)
        {
            NBTTagCompound compound = nbt.getCompoundTagAt(i);
            map.put(readFrom(compound, "key", keyClass), readFrom(compound, "value", valueClass));
        }
        return map.build();
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> void writeMapTo(NBTTagCompound compound, String key, Map<K, V> map)
    {
        compound.setTag(key, writeMap(map));
    }

    public static <K extends NBTCompoundObject, V extends NBTCompoundObject> NBTTagList writeMap(Map<K, V> map)
    {
        NBTTagList nbt = new NBTTagList();
        for (Map.Entry<K, V> entry : map.entrySet())
        {
            NBTTagCompound compound = new NBTTagCompound();
            writeTo(compound, "key", entry.getKey());
            writeTo(compound, "value", entry.getValue());
            nbt.appendTag(compound);
        }
        return nbt;
    }
}
