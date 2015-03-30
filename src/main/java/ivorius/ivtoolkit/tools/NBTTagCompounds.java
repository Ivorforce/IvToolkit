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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 30.03.15.
 */
public class NBTTagCompounds
{
    public static NBTTagList write(Iterable<? extends NBTCompoundObject> objects)
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

    public static <T extends NBTCompoundObject> List<T> readFrom(NBTTagCompound compound, String key, Class<T> tClass)
    {
        return read(compound.getTagList(key, Constants.NBT.TAG_COMPOUND), tClass);
    }

    public static <T extends NBTCompoundObject> List<T> read(NBTTagList list, Class<T> tClass)
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
