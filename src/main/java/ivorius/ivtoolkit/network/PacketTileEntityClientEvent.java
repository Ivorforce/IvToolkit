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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import ivorius.ivtoolkit.blocks.BlockPositions;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketTileEntityClientEvent implements IvPacket
{
    private DimensionType dimension;
    private BlockPos pos;
    private String context;
    private ByteBuf payload;

    public PacketTileEntityClientEvent()
    {
    }

    public PacketTileEntityClientEvent(DimensionType dimension, BlockPos pos, String context, ByteBuf payload)
    {
        this.dimension = dimension;
        this.pos = pos;
        this.context = context;
        this.payload = payload;
    }

    public static <ETileEntity extends TileEntity & ClientEventHandler> PacketTileEntityClientEvent packetEntityData(ETileEntity entity, String context, Object... params)
    {
        ByteBuf buf = Unpooled.buffer();
        entity.assembleClientEvent(buf, context, params);
        return new PacketTileEntityClientEvent(entity.getWorld().getDimension().getType(), entity.getPos(), context, buf);
    }

    public DimensionType getDimension()
    {
        return dimension;
    }

    public void setDimension(DimensionType dimension)
    {
        this.dimension = dimension;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public void setPos(BlockPos pos)
    {
        this.pos = pos;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public ByteBuf getPayload()
    {
        return payload;
    }

    public void setPayload(ByteBuf payload)
    {
        this.payload = payload;
    }

    @Override
    public void decode(PacketBuffer buf)
    {
        dimension = DimensionType.getById(buf.readInt());
        pos = BlockPositions.readFromBuffer(buf);
        context = buf.readString(1000);
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void encode(PacketBuffer buf)
    {
        buf.writeInt(dimension.getId());
        BlockPositions.writeToBuffer(pos, buf);
        buf.writeString(context);
        IvPacketHelper.writeByteBuffer(buf, payload);
    }
}
