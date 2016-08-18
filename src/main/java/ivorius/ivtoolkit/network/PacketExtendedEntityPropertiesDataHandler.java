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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketExtendedEntityPropertiesDataHandler extends SchedulingMessageHandler<PacketEntityCapabilityData, IMessage>
{
    @SideOnly(Side.CLIENT)
    @Override
    public void processClient(PacketEntityCapabilityData message, MessageContext ctx)
    {
        processClientC(message);
    }

    private <T> void processClientC(PacketEntityCapabilityData message)
    {
        World world = IvSideClient.getClientWorld();
        Entity entity = world.getEntityByID(message.getEntityID());

        if (entity != null)
        {
            Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(message.getCapabilityKey());
            T t = entity.getCapability(capability, message.getDirection());

            if (t != null)
                ((PartiallyUpdatableCapability<T>) capability).readUpdateData(t, message.getDirection(), message.getPayload(), message.getContext());
        }
    }
}
