/*
 *  Copyright (c) 2014, Lukas Tenbrink.
 *  * http://ivorius.net
 */

package ivorius.ivtoolkit.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SchedulingMessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY>
{
    @Override
    public REPLY onMessage(REQ message, MessageContext ctx)
    {
        if (ctx.side.isClient())
            Minecraft.getMinecraft().addScheduledTask(() -> processClient(message, ctx));
        else if (ctx.side.isServer())
        {
            WorldServer world = getServerWorld(message, ctx);
            world.addScheduledTask(() -> processServer(message, ctx, world));
        }

        return null;
    }

    public WorldServer getServerWorld(REQ message, MessageContext ctx)
    {
        return (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
    }

    public void processClient(REQ message, MessageContext ctx)
    {

    }

    public void processServer(REQ message, MessageContext ctx, WorldServer world)
    {

    }
}