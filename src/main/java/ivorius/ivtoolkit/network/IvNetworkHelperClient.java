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

package ivorius.ivtoolkit.network;

import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by lukas on 25.09.14.
 */
public class IvNetworkHelperClient
{
    @Deprecated
    public static <ETileEntity extends TileEntity & ClientEventHandler> void sendTileEntityUpdatePacket(ETileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params)
    {
        sendTileEntityEventPacket(tileEntity, context, network, params);
    }

    public static <ETileEntity extends TileEntity & ClientEventHandler> void sendTileEntityEventPacket(ETileEntity tileEntity, String context, SimpleNetworkWrapper network, Object... params)
    {
        if (!(tileEntity.getWorldObj().isRemote))
            throw new UnsupportedOperationException();

        network.sendToServer(PacketTileEntityClientEvent.packetEntityData(tileEntity, context, params));
    }
}
