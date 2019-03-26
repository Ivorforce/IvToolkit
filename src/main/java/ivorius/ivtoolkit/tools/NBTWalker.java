/*
 * Copyright 2016 Lukas Tenbrink
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

import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 21.06.16.
 */
public class NBTWalker
{
    public static boolean walk(INBTBase tag, Visitor<INBTBase> consumer)
    {
        if (tag instanceof NBTTagCompound)
            return walk((NBTTagCompound) tag, consumer);
        else if (tag instanceof NBTTagList)
            return walk((NBTTagList) tag, consumer);
        else
            return consumer.visit(tag);
    }

    public static boolean walk(NBTTagCompound compound, Visitor<INBTBase> consumer)
    {
        return consumer.visit(compound) && compound.keySet().stream().allMatch(key -> walk(compound.getTag(key), consumer));
    }

    public static boolean walk(NBTTagList list, Visitor<INBTBase> consumer)
    {
        return consumer.visit(list) && NBTTagLists.nbtBases(list).stream().allMatch(nbt -> walk(nbt, consumer));
    }

    public static boolean walkCompounds(INBTBase tag, Visitor<NBTTagCompound> consumer)
    {
        if (tag instanceof NBTTagCompound)
            return walkCompounds((NBTTagCompound) tag, consumer);
        else if (tag instanceof NBTTagList)
            return walkCompounds((NBTTagList) tag, consumer);
        else
            return true;
    }

    public static boolean walkCompounds(NBTTagCompound compound, Visitor<NBTTagCompound> consumer)
    {
        return consumer.visit(compound) && compound.keySet().stream().allMatch(key -> walkCompounds(compound.getTag(key), consumer));
    }

    public static boolean walkCompounds(NBTTagList list, Visitor<NBTTagCompound> consumer)
    {
        return list.getTagType() != Constants.NBT.TAG_COMPOUND || NBTTagLists.nbtBases(list).stream().allMatch(nbt -> walkCompounds(nbt, consumer));
    }
}
