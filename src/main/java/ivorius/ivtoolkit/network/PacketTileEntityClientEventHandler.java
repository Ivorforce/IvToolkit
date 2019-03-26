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

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketTileEntityClientEventHandler
{
    public static WorldServer getServerWorld(PacketTileEntityClientEvent message, NetworkEvent.Context ctx)
    {
        return ctx.getSender().getServerWorld().getServer().getWorld(message.getDimension());
    }

    public static void handle(PacketTileEntityClientEvent packet, Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();

        SchedulingMessageHandler.schedule(context, () -> handleServer(packet, context));
    }

    protected static <T> void handleServer(PacketTileEntityClientEvent message, NetworkEvent.Context context)
    {
        WorldServer world = getServerWorld(message, context);
        TileEntity entity = world.getTileEntity(message.getPos());

        if (entity != null)
            ((ClientEventHandler) entity).onClientEvent(message.getPayload(), message.getContext(), context.getSender());
    }
}
