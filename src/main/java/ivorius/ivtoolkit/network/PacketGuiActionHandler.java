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

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.inventory.Container;
import net.minecraft.network.NetHandlerPlayServer;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketGuiActionHandler implements IMessageHandler<PacketGuiAction, IMessage>
{
    @Override
    public IMessage onMessage(PacketGuiAction message, MessageContext ctx)
    {
        NetHandlerPlayServer netHandler = ctx.getServerHandler();

        Container container = netHandler.playerEntity.openContainer;
        if (container instanceof PacketGuiAction.ActionHandler)
        {
            ((PacketGuiAction.ActionHandler) container).handleAction(message.getContext(), message.getPayload());
        }

        return null;
    }
}
