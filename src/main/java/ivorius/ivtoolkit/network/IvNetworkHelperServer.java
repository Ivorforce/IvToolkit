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

import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import io.netty.channel.Channel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 02.07.14.
 */
public class IvNetworkHelperServer
{
    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network, EntityPlayer player)
    {
        if (!(player instanceof EntityPlayerMP))
            throw new UnsupportedOperationException();

        network.sendTo(PacketTileEntityData.packetEntityData(tileEntity, context), (EntityPlayerMP) player);
    }

    public static <UTileEntity extends TileEntity & PartialUpdateHandler> void sendTileEntityUpdatePacket(UTileEntity tileEntity, String context, SimpleNetworkWrapper network)
    {
        sendToPlayersWatchingChunk(tileEntity.getWorldObj(), tileEntity.xCoord / 16, tileEntity.zCoord / 16, network, PacketTileEntityData.packetEntityData(tileEntity, context));
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, SimpleNetworkWrapper network, IMessage message)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            network.sendTo(message, playerMP);
        }
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Channel channel, Object message)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            sendToPlayer(channel, playerMP, message);
        }
    }

    public static void sendToPlayersWatchingChunk(World world, int chunkX, int chunkZ, Packet packet)
    {
        List<EntityPlayerMP> playersWatching = getPlayersWatchingChunk(world, chunkX, chunkZ);

        for (EntityPlayerMP playerMP : playersWatching)
        {
            playerMP.playerNetServerHandler.sendPacket(packet);
        }
    }

    public static void sendToPlayer(Channel channel, EntityPlayerMP playerMP, Object message)
    {
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(playerMP);
        channel.writeAndFlush(message);
    }

    public static List<EntityPlayerMP> getPlayersWatchingChunk(World world, int chunkX, int chunkZ)
    {
        if (world.isRemote || !(world instanceof WorldServer))
        {
            return Collections.emptyList();
        }

        ArrayList<EntityPlayerMP> playersWatching = new ArrayList<>();

        WorldServer server = (WorldServer) world;
        PlayerManager playerManager = server.getPlayerManager();

        List<EntityPlayerMP> players = server.playerEntities;
        for (EntityPlayerMP player : players)
        {
            if (playerManager.isPlayerWatchingChunk(player, chunkX, chunkZ))
            {
                playersWatching.add(player);
            }
        }

        return playersWatching;
    }
}
