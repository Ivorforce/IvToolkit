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
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * Created by lukas on 01.07.14.
 */
public class PacketEntityCapabilityData implements IMessage
{
    private int entityID;
    private String context;
    private String capabilityKey;
    private EnumFacing direction;
    private ByteBuf payload;

    public PacketEntityCapabilityData()
    {
    }

    public PacketEntityCapabilityData(int entityID, String context, String capabilityKey, EnumFacing direction, ByteBuf payload)
    {
        this.entityID = entityID;
        this.context = context;
        this.capabilityKey = capabilityKey;
        this.direction = direction;
        this.payload = payload;
    }

    public static <T> PacketEntityCapabilityData packetEntityData(Entity entity, String capabilityKey, EnumFacing facing, String context, Object... params)
    {
        Capability<T> capability = CapabilityUpdateRegistry.INSTANCE.capability(capabilityKey);
        T eep = entity.getCapability(capability, facing);

        if (!(eep instanceof PartialUpdateHandler))
            throw new IllegalArgumentException("Capability must implement PartialUpdateHandler to send update packets!");

        ByteBuf buf = Unpooled.buffer();
        ((PartialUpdateHandler) eep).writeUpdateData(buf, context, params);

        return new PacketEntityCapabilityData(entity.getEntityId(), context, capabilityKey, facing, buf);
    }

    public int getEntityID()
    {
        return entityID;
    }

    public void setEntityID(int entityID)
    {
        this.entityID = entityID;
    }

    public String getContext()
    {
        return context;
    }

    public void setContext(String context)
    {
        this.context = context;
    }

    public String getCapabilityKey()
    {
        return capabilityKey;
    }

    public void setCapabilityKey(String capabilityKey)
    {
        this.capabilityKey = capabilityKey;
    }

    public EnumFacing getDirection()
    {
        return direction;
    }

    public void setDirection(EnumFacing direction)
    {
        this.direction = direction;
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
    public void fromBytes(ByteBuf buf)
    {
        entityID = buf.readInt();
        context = ByteBufUtils.readUTF8String(buf);
        capabilityKey = ByteBufUtils.readUTF8String(buf);
        direction = IvPacketHelper.maybeRead(buf, null, () -> direction = EnumFacing.getFront(buf.readInt()));
        payload = IvPacketHelper.readByteBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(entityID);
        ByteBufUtils.writeUTF8String(buf, context);
        ByteBufUtils.writeUTF8String(buf, capabilityKey);
        IvPacketHelper.maybeWrite(buf, direction, () -> direction.getIndex());
        IvPacketHelper.writeByteBuffer(buf, payload);
    }

}
