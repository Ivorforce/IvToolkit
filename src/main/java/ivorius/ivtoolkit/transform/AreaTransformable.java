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

package ivorius.ivtoolkit.transform;

/**
 * Implement this if your TileEntity or Entity needs special treatment when being transformed.
 */
public interface AreaTransformable
{
    /**
     * Transforms the object inside a certain bounds.
     * It is expected that x is mirrored first (if mirror), and only afterwards the rotation is applied.
     * Note that implementing this will delete default transforming functionality, so you need to do that yourself as well.
     *
     * @param rotation Clockwise rotations to perform.
     * @param mirrorX If x is being mirrored.
     * @param inBounds The bounds of the area the transformation should take place in.
     */
    void transform(int rotation, boolean mirrorX, int[] inBounds);
}
