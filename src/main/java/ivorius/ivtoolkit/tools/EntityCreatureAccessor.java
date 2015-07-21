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

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lukas on 10.03.15.
 */
public class EntityCreatureAccessor
{
    protected static Field maximumHomeDistance;

    protected static Field maximumHomeDistance()
    {
        return maximumHomeDistance != null
                ? maximumHomeDistance
                : (maximumHomeDistance = ReflectionHelper.findField(EntityCreature.class, "maximumHomeDistance", "field_70772_bD"));
    }

    public static void setHomePosition(EntityCreature creature, BlockPos pos)
    {
        float currentHomeDistance = creature.getMaximumHomeDistance();
        creature.setHomePosAndDistance(pos, (int) currentHomeDistance);
        setMaximumHomeDistance(creature, currentHomeDistance); // Fix for setHomeArea taking an int
    }

    public static float getMaximumHomeDistance(EntityCreature creature)
    {
        try
        {
            return maximumHomeDistance().getFloat(creature);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        return -1f;
    }

    public static void setMaximumHomeDistance(EntityCreature creature, float maximumHomeDistance)
    {
        try
        {
            maximumHomeDistance().setFloat(creature, maximumHomeDistance);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
