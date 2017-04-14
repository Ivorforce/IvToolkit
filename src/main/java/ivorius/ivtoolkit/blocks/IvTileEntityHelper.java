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

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class IvTileEntityHelper
{
    public static SPacketUpdateTileEntity getStandardDescriptionPacket(TileEntity tileEntity)
    {
        NBTTagCompound var1 = new NBTTagCompound();
        tileEntity.writeToNBT(var1);
        return new SPacketUpdateTileEntity(tileEntity.getPos(), 1, var1);
    }

    public static World getAnyWorld()
    {
        return FMLCommonHandler.instance().getSide().isClient()
                ? IvSideClient.getClientWorld()
                : FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld();
    }
}
