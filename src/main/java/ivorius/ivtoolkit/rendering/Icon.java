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

package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.math.IvMathHelper;

/**
 * Created by lukas on 21.07.15.
 */
public class Icon
{
    private float minU, maxU;
    private float minV, maxV;

    private Icon(float minU, float maxU, float minV, float maxV)
    {
        this.minU = minU;
        this.maxU = maxU;
        this.minV = minV;
        this.maxV = maxV;
    }

    public static Icon fromCoords(float minU, float maxU, float minV, float maxV)
    {
        return new Icon(minU, maxU, minV, maxV);
    }

    public static Icon fromSize(float minU, float sizeU, float minV, float sizeV)
    {
        return new Icon(minU, minU + sizeU, minV, minV + sizeV);
    }

    public float getMinU()
    {
        return minU;
    }

    public float getMaxU()
    {
        return maxU;
    }

    public float getMinV()
    {
        return minV;
    }

    public float getMaxV()
    {
        return maxV;
    }

    public float getU(float lerp)
    {
        return IvMathHelper.mix(minU, maxU, lerp);
    }

    public float getV(float lerp)
    {
        return IvMathHelper.mix(minV, maxV, lerp);
    }
}
