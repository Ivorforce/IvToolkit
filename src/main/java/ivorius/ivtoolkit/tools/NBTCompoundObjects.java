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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTCompoundObjects
{
    public static <T extends NBTCompoundObject> Function<NBTTagCompound, T> readFunction(final Class<T> tClass)
    {
        return new Function<NBTTagCompound, T>()
        {
            @Nullable
            @Override
            public T apply(NBTTagCompound input)
            {
                return read(input, tClass);
            }
        };
    }

    public static Function<? extends NBTCompoundObject, NBTTagCompound> writeFunction()
    {
        return new Function<NBTCompoundObject, NBTTagCompound>()
        {
            @Nullable
            @Override
            public NBTTagCompound apply(@Nullable NBTCompoundObject input)
            {
                return write(input);
            }
        };
    }

    public static void writeListTo(NBTTagCompound compound, String key, Iterable<? extends NBTCompoundObject> objects)
    {
        compound.setTag(key, writeList(objects));
    }

    public static <T extends NBTCompoundObject> List<T> readListFrom(NBTTagCompound compound, String key, Class<T> tClass)
    {
        return readList(compound.getTagList(key, Constants.NBT.TAG_COMPOUND), tClass);
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

    public static <T extends NBTCompoundObject> List<T> readList(NBTTagList list, Class<T> tClass)
    {
        List<T> rList = new ArrayList<>(list.tagCount());

        for (int i = 0; i < list.tagCount(); i++)
            rList.add(read(list.getCompoundTagAt(i), tClass));

        return rList;
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

    public static NBTTagCompound write(NBTCompoundObject compoundObject)
    {
        NBTTagCompound compound = new NBTTagCompound();
        compoundObject.writeToNBT(compound);
        return compound;
    }
}
