/*
 * Copyright 2014 Lukas Tenbrink
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

package ivorius.ivtoolkit.blocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class IvTileEntityHelper
{
    public static Packet getStandardDescriptionPacket(TileEntity tileEntity)
    {
        NBTTagCompound var1 = new NBTTagCompound();
        tileEntity.writeToNBT(var1);
        return new S35PacketUpdateTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 1, var1);
    }
}
