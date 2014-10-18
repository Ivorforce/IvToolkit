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

package ivorius.ivtoolkit.models.textures;

import ivorius.ivtoolkit.models.utils.MathUtils;

/**
 * Defines a rectangular area of a texture. The coordinate system used has its origin in the upper left corner with the x-axis
 * pointing to the right and the y axis pointing downwards.
 *
 * @author mzechner
 * @author Nathan Sweet
 */
public class TextureSub implements Texture
{
    private Texture parent;
    private float minU, minV, maxU, maxV;

    public TextureSub(Texture parent, float minU, float minV, float maxU, float maxV)
    {
        this.parent = parent;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
    }

    @Override
    public void bindTexture()
    {
        parent.bindTexture();
    }

    @Override
    public float minU()
    {
        return MathUtils.mix(parent.minU(), parent.maxU(), minU);
    }

    @Override
    public float maxU()
    {
        return MathUtils.mix(parent.minU(), parent.maxU(), maxU);
    }

    @Override
    public float minV()
    {
        return MathUtils.mix(parent.minV(), parent.maxV(), minV);
    }

    @Override
    public float maxV()
    {
        return MathUtils.mix(parent.minV(), parent.maxV(), maxV);
    }
}
