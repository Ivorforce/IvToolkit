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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SchedulingMessageHandler<REQ extends IMessage, REPLY extends IMessage> implements IMessageHandler<REQ, REPLY>
{
    @Override
    public REPLY onMessage(REQ message, MessageContext ctx)
    {
        if (ctx.side.isClient())
            onMessageClient(message, ctx);
        else if (ctx.side.isServer())
            onMessageServer(message, ctx);

        return null;
    }

    private void onMessageServer(REQ message, MessageContext ctx)
    {
        WorldServer world = getServerWorld(message, ctx);
        world.addScheduledTask(() -> processServer(message, ctx, world));
    }

    @SideOnly(Side.CLIENT)
    private void onMessageClient(REQ message, MessageContext ctx)
    {
        Minecraft.getMinecraft().addScheduledTask(() -> processClient(message, ctx));
    }

    public WorldServer getServerWorld(REQ message, MessageContext ctx)
    {
        return (WorldServer) ctx.getServerHandler().playerEntity.world;
    }

    @SideOnly(Side.CLIENT)
    public void processClient(REQ message, MessageContext ctx)
    {

    }

    public void processServer(REQ message, MessageContext ctx, WorldServer world)
    {

    }
}