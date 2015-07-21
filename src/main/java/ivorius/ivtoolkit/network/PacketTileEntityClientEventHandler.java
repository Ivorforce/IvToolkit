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

import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketTileEntityClientEventHandler implements IMessageHandler<PacketTileEntityClientEvent, IMessage>
{
    @Override
    public IMessage onMessage(PacketTileEntityClientEvent message, MessageContext ctx)
    {
        World world = MinecraftServer.getServer().worldServerForDimension(message.getDimension());
        TileEntity entity = world.getTileEntity(message.getPos());

        if (entity != null)
            ((ClientEventHandler) entity).onClientEvent(message.getPayload(), message.getContext(), ctx.getServerHandler().playerEntity);

        return null;
    }
}
