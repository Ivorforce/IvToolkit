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

import ivorius.ivtoolkit.api.IvSaveAPI;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by lukas on 12.06.16.
 */
public class NBTCompoundObjectsMC
{
    public static NBTTagCompound write(Entity entity)
    {
        return write(entity, false);
    }

    public static NBTTagCompound write(Entity entity, boolean worldIndependent)
    {
        NBTTagCompound compound = new NBTTagCompound();

        if (IvSaveAPI.canWriteEntityToTag(entity))
            IvSaveAPI.writeEntityToTag(entity, compound, worldIndependent);
        else
            entity.writeToNBTOptional(compound);

        return compound;
    }

    public static NBTTagCompound write(TileEntity entity)
    {
        return write(entity, false);
    }

    public static NBTTagCompound write(TileEntity entity, boolean worldIndependent)
    {
        NBTTagCompound compound = new NBTTagCompound();

        if (IvSaveAPI.canWriteTileEntityToTag(entity))
            IvSaveAPI.writeTileEntityToTag(entity, compound, worldIndependent);
        else
            entity.writeToNBT(compound);

        return compound;
    }
}
