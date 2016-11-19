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

package ivorius.ivtoolkit.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 17.01.15.
 */
public class IvItemStacks
{
    public static int getNBT(ItemStack stack, String key, int defaultValue)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, Constants.NBT.TAG_INT)
                ? stack.getTagCompound().getInteger(key)
                : defaultValue;
    }

    public static long getNBT(ItemStack stack, String key, long defaultValue)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, Constants.NBT.TAG_LONG)
                ? stack.getTagCompound().getLong(key)
                : defaultValue;
    }

    public static float getNBT(ItemStack stack, String key, float defaultValue)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, Constants.NBT.TAG_FLOAT)
                ? stack.getTagCompound().getFloat(key)
                : defaultValue;
    }

    public static double getNBT(ItemStack stack, String key, double defaultValue)
    {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(key, Constants.NBT.TAG_DOUBLE)
                ? stack.getTagCompound().getDouble(key)
                : defaultValue;
    }
}
