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

import net.minecraft.util.BlockPos;

/**
 * Implement this if your TileEntity or Entity needs special treatment when being moved.
 */
public interface Movable
{
    /**
     * Moves the object by the specified amount.
     * Note that implementing this will delete default moving functionality, so you need to do that yourself.
     *
     * @param pos The distance moved
     */
    void move(BlockPos pos);
}
