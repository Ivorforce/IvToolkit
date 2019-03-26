/*
 * Copyright 2019 Lukas Tenbrink
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

import ivorius.ivtoolkit.IvToolkit;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public interface IvPacket
{
    static <T extends IvPacket> void register(SimpleChannel channel, int id, Class<T> packet, BiConsumer<T, Supplier<NetworkEvent.Context>> handler)
    {
        channel.registerMessage(id, packet,
                T::encode,
                buffer -> {
                    try {
                        T t = packet.newInstance();
                        t.decode(buffer);
                        return t;
                    }
                    catch (InstantiationException | IllegalAccessException e) {
                        IvToolkit.logger.error("Wrong Implementation!", e);
                    }

                    return null;
                },
                handler
        );
    }

    void decode(PacketBuffer buffer);

    void encode(PacketBuffer buffer);
}
