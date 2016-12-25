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

import net.minecraft.entity.EntityHanging;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lukas on 21.07.15.
 */
public class EntityHangingAccessor
{
    protected static Method hangingSetDirection;

    protected static Method hangingSetDirection()
    {
        return hangingSetDirection != null
                ? hangingSetDirection
                : (hangingSetDirection = ReflectionHelper.findMethod(EntityHanging.class, null, new String[]{"updateFacingWithBoundingBox", "func_174859_a"}, EnumFacing.class));
    }

    public static void setHangingDirection(EntityHanging entity, EnumFacing facing)
    {
        try
        {
            hangingSetDirection().invoke(entity, facing);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
        }
    }
}
