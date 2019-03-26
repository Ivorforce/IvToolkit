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

import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketEntityDataHandler
{
    public static void handle(PacketEntityCapabilityData packet, Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();

        SchedulingMessageHandler.schedule(context, () -> handleClient(packet, context));
    }

    @OnlyIn(Dist.CLIENT)
    protected static <T> void handleClient(PacketEntityCapabilityData message, NetworkEvent.Context context)
    {
        World world = IvSideClient.getClientWorld();
        Entity entity = world.getEntityByID(message.getEntityID());

        if (entity != null)
            ((PartialUpdateHandler) entity).readUpdateData(message.getPayload(), message.getContext());
    }
}
