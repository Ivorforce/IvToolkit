/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.ivtoolkit.models.utils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * Class with static helper methods to increase the speed of array/direct buffer and direct buffer/direct buffer transfers
 *
 * @author mzechner, xoppa
 */
public final class BufferUtils
{
    public static void copy(float[] vertices, ByteBuffer byteBuffer, int numFloats, int offset)
    {
        byteBuffer.position(0);
        byteBuffer.limit(numFloats << 2);

        for (int index = 0; index < numFloats; index ++)
            byteBuffer.putFloat(vertices[index + offset]);

        byteBuffer.position(0);
    }

    public static void copy(float[] vertices, FloatBuffer floatBuffer, int numFloats, int offset)
    {
        floatBuffer.position(0);
        floatBuffer.limit(numFloats);

        for (int index = 0; index < numFloats; index ++)
            floatBuffer.put(vertices[index + offset]);

        floatBuffer.position(0);
    }

    public static void copy(float[] vertices, int sourceOffset, int count, ByteBuffer byteBuffer)
    {
        for (int index = 0; index < count; index ++)
            byteBuffer.putFloat(vertices[index + sourceOffset]);
    }

    public static String toString(FloatBuffer floatBuffer)
    {
        StringBuilder builder = new StringBuilder();
        for (int i = floatBuffer.position(); i < floatBuffer.limit(); i++)
            builder.append(i == floatBuffer.position() ? "" : ", ").append(floatBuffer.get(i));
        return builder.toString();
    }
}
