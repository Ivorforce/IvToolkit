/*
 * Copyright 2016 Lukas Tenbrink
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
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

/**
 * A interface for some object types that need extra information to be communicated
 * between the server and client when their values are updated.
 */
public interface PartiallyUpdatableCapability<T>
{
    /**
     * Called on the server when constructing the update packet.
     * Data should be added to the provided stream.
     *
     * @param buffer The packet data stream
     */
    void writeUpdateData(T t, EnumFacing facing, ByteBuf buffer, String context, Object... params);

    /**
     * Called on the client when it receives an update packet.
     * Data should be read out of the stream in the same way as it was written.
     *
     * @param buffer The packet data stream
     */
    void readUpdateData(T t, EnumFacing facing, ByteBuf buffer, String context);
}
