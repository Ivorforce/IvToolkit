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

/**
 * Created by lukas on 01.07.14.
 */
public class IvPacketHelper
{
    public static void writeByteBuffer(ByteBuf dst, ByteBuf src)
    {
        int length = src.readableBytes();
        dst.writeInt(length).writeBytes(src, 0, length);
    }

    public static ByteBuf readByteBuffer(ByteBuf src)
    {
        int length = src.readInt();
        return src.readBytes(length);
    }

    public static void writeNumber(ByteBuf buffer, Number number)
    {
        if (number instanceof Byte)
        {
            buffer.writeByte((Byte) number);
        }
        else if (number instanceof Double)
        {
            buffer.writeDouble((Double) number);
        }
        else if (number instanceof Float)
        {
            buffer.writeFloat((Float) number);
        }
        else if (number instanceof Integer)
        {
            buffer.writeInt((Integer) number);
        }
        else if (number instanceof Long)
        {
            buffer.writeLong((Long) number);
        }
        else if (number instanceof Short)
        {
            buffer.writeShort((Short) number);
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }
}
