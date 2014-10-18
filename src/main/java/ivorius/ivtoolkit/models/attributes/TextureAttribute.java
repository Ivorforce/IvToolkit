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

package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;
import ivorius.ivtoolkit.models.textures.Texture;
import ivorius.ivtoolkit.models.textures.TextureSub;

public class TextureAttribute extends Attribute
{
    public final static String DiffuseAlias = "diffuseTexture";
    public final static long Diffuse = register(DiffuseAlias);
    public final static String SpecularAlias = "specularTexture";
    public final static long Specular = register(SpecularAlias);
    public final static String BumpAlias = "bumpTexture";
    public final static long Bump = register(BumpAlias);
    public final static String NormalAlias = "normalTexture";
    public final static long Normal = register(NormalAlias);

    protected static long Mask = Diffuse | Specular | Bump | Normal;

    public final static boolean is(final long mask)
    {
        return (mask & Mask) != 0;
    }

    public static TextureAttribute createDiffuse(final Texture texture)
    {
        return new TextureAttribute(Diffuse, texture);
    }

    public static TextureAttribute createDiffuse(final TextureSub region)
    {
        return new TextureAttribute(Diffuse, region);
    }

    public static TextureAttribute createSpecular(final Texture texture)
    {
        return new TextureAttribute(Specular, texture);
    }

    public static TextureAttribute createSpecular(final TextureSub region)
    {
        return new TextureAttribute(Specular, region);
    }

    public static TextureAttribute createNormal(final Texture texture)
    {
        return new TextureAttribute(Normal, texture);
    }

    public static TextureAttribute createNormal(final TextureSub region)
    {
        return new TextureAttribute(Normal, region);
    }

    public static TextureAttribute createBump(final Texture texture)
    {
        return new TextureAttribute(Bump, texture);
    }

    public final Texture texture;

    public TextureAttribute(final long type, final Texture texture)
    {
        super(type);
        if (!is(type))
            throw new RuntimeException("Invalid type specified");

        this.texture = texture;
    }

    @Override
    public Attribute copy()
    {
        return new TextureAttribute(type, texture);
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 991 * result + texture.hashCode();
//		result = 991 * result + NumberUtils.floatToRawIntBits(offsetU);
//		result = 991 * result + NumberUtils.floatToRawIntBits(offsetV);
//		result = 991 * result + NumberUtils.floatToRawIntBits(scaleU);
//		result = 991 * result + NumberUtils.floatToRawIntBits(scaleV);
        return result;
    }
}
