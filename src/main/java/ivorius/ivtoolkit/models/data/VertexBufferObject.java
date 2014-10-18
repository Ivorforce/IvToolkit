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

import ivorius.ivtoolkit.models.utils.BufferUtils;
import net.minecraft.client.renderer.GLAllocation;
import org.lwjgl.opengl.GL15;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * <p>
 * A {@link VertexData} implementation based on OpenGL vertex buffer objects.
 * </p>
 * <p/>
 * <p>
 * If the OpenGL ES context was lost you can call {@link #invalidate()} to recreate a new OpenGL vertex buffer object. This class
 * can be used seamlessly with OpenGL ES 1.x and 2.0.
 * </p>
 * <p/>
 * <p>
 * In case OpenGL ES 2.0 is used in the application the data is bound via glVertexAttribPointer() according to the attribute
 * aliases specified via {@link VertexAttributes} in the constructor.
 * </p>
 * <p/>
 * <p>
 * Uses indirect Buffers on Android 1.5/1.6 to fix GC invocation due to leaking PlatformAddress instances.
 * </p>
 * <p/>
 * <p>
 * VertexBufferObjects must be disposed via the {@link #dispose()} method when no longer needed
 * </p>
 *
 * @author mzechner, Dave Clayton <contact@redskyforge.com>
 */
public class VertexBufferObject implements VertexData
{
    final VertexAttributes attributes;
    final FloatBuffer buffer;
    final ByteBuffer byteBuffer;
    int bufferHandle;
    final boolean isStatic;
    final int usage;
    boolean isDirty = false;
    boolean isBound = false;

    /**
     * Constructs a new interleaved VertexBufferObject.
     *
     * @param isStatic    whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes  the {@link VertexAttribute}s.
     */
    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttribute... attributes)
    {
        this(isStatic, numVertices, new VertexAttributes(attributes));
    }

    /**
     * Constructs a new interleaved VertexBufferObject.
     *
     * @param isStatic    whether the vertex data is static.
     * @param numVertices the maximum number of vertices
     * @param attributes  the {@link VertexAttributes}.
     */
    public VertexBufferObject(boolean isStatic, int numVertices, VertexAttributes attributes)
    {
        this.isStatic = isStatic;
        this.attributes = attributes;

        byteBuffer = GLAllocation.createDirectByteBuffer(this.attributes.vertexSize * numVertices);
        buffer = byteBuffer.asFloatBuffer();
        buffer.flip();
        byteBuffer.flip();
        bufferHandle = createBufferObject();
        usage = isStatic ? GL15.GL_STATIC_DRAW : GL15.GL_DYNAMIC_DRAW;
    }

    private int createBufferObject()
    {
        return GL15.glGenBuffers();
    }

    @Override
    public VertexAttributes getAttributes()
    {
        return attributes;
    }

    @Override
    public int getNumVertices()
    {
        return buffer.limit() * 4 / attributes.vertexSize;
    }

    @Override
    public int getNumMaxVertices()
    {
        return byteBuffer.capacity() / attributes.vertexSize;
    }

    @Override
    public FloatBuffer getBuffer()
    {
        isDirty = true;
        return buffer;
    }

    private void bufferChanged()
    {
        if (isBound)
        {
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, usage);
            isDirty = false;
        }
    }

    @Override
    public void setVertices(float[] vertices, int offset, int count)
    {
        isDirty = true;
        BufferUtils.copy(vertices, byteBuffer, count, offset);
        buffer.position(0);
        buffer.limit(count);
        bufferChanged();
    }

    @Override
    public void updateVertices(int targetOffset, float[] vertices, int sourceOffset, int count)
    {
        isDirty = true;
        final int pos = byteBuffer.position();
        byteBuffer.position(targetOffset * 4);
        BufferUtils.copy(vertices, sourceOffset, count, byteBuffer);
        byteBuffer.position(pos);
        buffer.position(0);
        bufferChanged();
    }

    /**
     * Invalidates the VertexBufferObject so a new OpenGL buffer handle is created. Use this in case of a context loss.
     */
    public void invalidate()
    {
        bufferHandle = createBufferObject();
        isDirty = true;
    }

    /**
     * Disposes of all resources this VertexBufferObject uses.
     */
    @Override
    public void dispose()
    {
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        if (bufferHandle > 0)
            GL15.glDeleteBuffers(bufferHandle);
        bufferHandle = 0;
    }
}
