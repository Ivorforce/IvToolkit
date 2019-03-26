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

package ivorius.ivtoolkit.models.data;

import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class IndexBufferObject implements IndexData
{
    ShortBuffer buffer;
    ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isDirect;
    boolean isDirty = true;
    boolean isBound = false;
    final int usage;

    /**
     * Creates a new IndexBufferObject.
     *
     * @param isStatic   whether the index buffer is static
     * @param maxIndices the maximum number of indices this buffer can hold
     */
    public IndexBufferObject(boolean isStatic, int maxIndices)
    {
        byteBuffer = GLAllocation.createDirectByteBuffer(maxIndices * 2);
        isDirect = true;

        buffer = byteBuffer.asShortBuffer();
        buffer.flip();
        byteBuffer.flip();
        bufferHandle = createBufferObject();
        usage = isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW;
    }

    /**
     * Creates a new IndexBufferObject to be used with vertex arrays.
     *
     * @param maxIndices the maximum number of indices this buffer can hold
     */
    public IndexBufferObject(int maxIndices)
    {
        byteBuffer = GLAllocation.createDirectByteBuffer(maxIndices * 2);
        this.isDirect = true;

        buffer = byteBuffer.asShortBuffer();
        buffer.flip();
        byteBuffer.flip();
        bufferHandle = createBufferObject();
        usage = GL15.GL_STATIC_DRAW;
    }

    private int createBufferObject()
    {
        return GL15.glGenBuffers();
    }

    /**
     * @return the number of indices currently stored in this buffer
     */
    public int getNumIndices()
    {
        return buffer.limit();
    }

    /**
     * @return the maximum number of indices this IndexBufferObject can store.
     */
    public int getNumMaxIndices()
    {
        return buffer.capacity();
    }

    /**
     * <p>
     * Sets the indices of this IndexBufferObject, discarding the old indices. The count must equal the number of indices to be
     * copied to this IndexBufferObject.
     * </p>
     * <p/>
     * <p>
     * This can be called in between calls to {@link #bind()} and {@link #unbind()}. The index data will be updated instantly.
     * </p>
     *
     * @param indices the vertex data
     * @param offset  the offset to start copying the data from
     * @param count   the number of shorts to copy
     */
    public void setIndices(short[] indices, int offset, int count)
    {
        isDirty = true;
        buffer.clear();
        buffer.put(indices, offset, count);
        buffer.flip();
        byteBuffer.position(0);
        byteBuffer.limit(count << 1);

        if (isBound)
        {
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, byteBuffer, usage);
            isDirty = false;
        }
    }

    /**
     * <p>
     * Returns the underlying ShortBuffer. If you modify the buffer contents they wil be uploaded on the call to {@link #bind()}.
     * If you need immediate uploading use {@link #setIndices(short[], int, int)}.
     * </p>
     *
     * @return the underlying short buffer.
     */
    public ShortBuffer getBuffer()
    {
        isDirty = true;
        return buffer;
    }

    /**
     * Binds this IndexBufferObject for rendering with glDrawElements.
     */
    public void bind()
    {
        if (bufferHandle == 0) throw new RuntimeException("No buffer allocated!");

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, bufferHandle);
        if (isDirty)
        {
            byteBuffer.limit(buffer.limit() * 2);
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, byteBuffer, usage);
            isDirty = false;
        }
        isBound = true;
    }

    /**
     * Unbinds this IndexBufferObject.
     */
    public void unbind()
    {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        isBound = false;
    }

    /**
     * Invalidates the IndexBufferObject so a new OpenGL buffer handle is created. Use this in case of a context loss.
     */
    public void invalidate()
    {
        bufferHandle = createBufferObject();
        isDirty = true;
    }

    @Override
    public void dispose()
    {
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        if (bufferHandle > 0)
            GL15.glDeleteBuffers(bufferHandle);
        bufferHandle = 0;
    }
}
