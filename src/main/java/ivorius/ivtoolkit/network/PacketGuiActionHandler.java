/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
