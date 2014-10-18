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

import ivorius.ivtoolkit.models.data.VertexAttributes.Usage;
import org.lwjgl.opengl.GL11;

public final class VertexAttribute
{
    /**
     * the attribute {@link Usage} *
     */
    public final int usage;
    /**
     * the number of components this attribute has *
     */
    public final int numComponents;
    /**
     * whether the values are normalized to either -1f and +1f (signed) or 0f and +1f (unsigned)
     */
    public final boolean normalized;
    /**
     * the OpenGL type of each component, e.g. GL11.GL_FLOAT or GL11.GL_UNSIGNED_BYTE
     */
    public final int type;
    /**
     * the offset of this attribute in bytes, don't change this! *
     */
    public int offset;
    /**
     * optional unit/index specifier, used for texture coordinates and bone weights *
     */
    public int unit;
    private final int usageIndex;

    /**
     * Constructs a new VertexAttribute.
     *
     * @param usage         the usage, used for the fixed function pipeline. Generic attributes are not supported in the fixed function
     *                      pipeline.
     * @param numComponents the number of components of this attribute, must be between 1 and 4.
     */
    public VertexAttribute(int usage, int numComponents)
    {
        this(usage, numComponents, 0);
    }

    /**
     * Constructs a new VertexAttribute.
     *
     * @param usage         the usage, used for the fixed function pipeline. Generic attributes are not supported in the fixed function
     *                      pipeline.
     * @param numComponents the number of components of this attribute, must be between 1 and 4.
     * @param index         unit/index of the attribute, used for boneweights and texture coordinates.
     */
    public VertexAttribute(int usage, int numComponents, int index)
    {
        this(usage, numComponents, usage == Usage.ColorPacked ? GL11.GL_UNSIGNED_BYTE : GL11.GL_FLOAT,
                usage == Usage.ColorPacked, index);
    }

    private VertexAttribute(int usage, int numComponents, int type, boolean normalized)
    {
        this(usage, numComponents, type, normalized, 0);
    }

    private VertexAttribute(int usage, int numComponents, int type, boolean normalized, int index)
    {
        this.usage = usage;
        this.numComponents = numComponents;
        this.type = type;
        this.normalized = normalized;
        this.unit = index;
        this.usageIndex = Integer.numberOfTrailingZeros(usage);
    }

    public static VertexAttribute Position () {
        return new VertexAttribute(Usage.Position, 3);
    }

    public static VertexAttribute TexCoords (int unit) {
        return new VertexAttribute(Usage.TextureCoordinates, 2, unit);
    }

    public static VertexAttribute Normal () {
        return new VertexAttribute(Usage.Normal, 3);
    }

    public static VertexAttribute ColorPacked () {
        return new VertexAttribute(Usage.ColorPacked, 4, GL11.GL_UNSIGNED_BYTE, true);
    }

    public static VertexAttribute ColorUnpacked () {
        return new VertexAttribute(Usage.Color, 4, GL11.GL_FLOAT, false);
    }

    public static VertexAttribute Tangent () {
        return new VertexAttribute(Usage.Tangent, 3);
    }

    public static VertexAttribute Binormal () {
        return new VertexAttribute(Usage.BiNormal, 3);
    }


    public static VertexAttribute BoneWeight(int unit)
    {
        return new VertexAttribute(Usage.BoneWeight, 2, unit);
    }

    /**
     * @return A unique number specifying the usage index (3 MSB) and unit (1 LSB).
     */
    public int getKey()
    {
        return (usageIndex << 8) + (unit & 0xFF);
    }
}
