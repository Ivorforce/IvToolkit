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

package ivorius.ivtoolkit.api;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by lukas on 26.12.16.
 */
public class IvSaveAPI
{
    private static Method getWriteTileEntityToTag(TileEntity entity) throws NoSuchMethodException
    {
        return entity.getClass().getDeclaredMethod("writeTileEntityToTag", NBTTagCompound.class, boolean.class);
    }

    public static boolean canWriteTileEntityToTag(TileEntity entity)
    {
        try
        {
            getWriteTileEntityToTag(entity);
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public static NBTTagCompound writeTileEntityToTag(TileEntity entity, NBTTagCompound compound, boolean worldIndependent)
    {
        try
        {
            return (NBTTagCompound) getWriteTileEntityToTag(entity).invoke(entity, compound, worldIndependent);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new IllegalStateException(e);
        }
    }

    private static Method getWriteEntityToTag(Entity entity) throws NoSuchMethodException
    {
        return entity.getClass().getDeclaredMethod("writeEntityToTag", NBTTagCompound.class, boolean.class);
    }

    public static boolean canWriteEntityToTag(Entity entity)
    {
        try
        {
            getWriteEntityToTag(entity);
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public static NBTTagCompound writeEntityToTag(Entity entity, NBTTagCompound compound, boolean worldIndependent)
    {
        try
        {
            return (NBTTagCompound) getWriteEntityToTag(entity).invoke(entity, compound, worldIndependent);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new IllegalStateException(e);
        }
    }
}
