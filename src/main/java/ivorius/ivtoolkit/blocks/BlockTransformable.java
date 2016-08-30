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

package ivorius.ivtoolkit.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Implement this if your Block needs special treatment when being transformed.
 *
 * This method is usually called post-default transform - the block state will already be transformed at this point.
 * Only implement this if you have special positioning data in your tile entity that also needs transformation.
 */
public interface BlockTransformable
{
    /**
     * Transforms the object inside a certain bounds.
     * Note that the mirror should be applied first.
     *
     * @param rotation The amount of times to rotate.
     * @param mirrorX If x is being mirrored.
     */
    void transform(World world, BlockPos pos, IBlockState state, int rotation, boolean mirrorX);
}
