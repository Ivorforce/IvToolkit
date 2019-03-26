/*
 * Copyright 2017 Lukas Tenbrink
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

package ivorius.ivtoolkit.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;

/**
 * Created by lukas on 14.04.17.
 */
public class WorldCache
{
    public final World world;

    private MutableBoundingBox boundingBox;
    private IBlockState[] states;

    public WorldCache(World world, MutableBoundingBox boundingBox)
    {
        this.world = world;
        this.boundingBox = boundingBox;
        states = new IBlockState[boundingBox.getXSize() * boundingBox.getYSize() * boundingBox.getZSize()];
    }

    protected Integer getIndex(BlockPos pos)
    {
        if (!boundingBox.isVecInside(pos))
            return null;

        return ((pos.getX() - boundingBox.minX) * boundingBox.getYSize()
                + (pos.getY() - boundingBox.minY)) * boundingBox.getZSize()
                + (pos.getZ() - boundingBox.minZ);
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        Integer index = getIndex(pos);

        if (index == null)
            return world.getBlockState(pos);

        IBlockState state = states[index];

        return state != null ? state : (states[index] = world.getBlockState(pos));
    }

    public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
    {
        boolean b = world.setBlockState(pos, state, flags);

        if (b) {
            Integer index = getIndex(pos);
            if (index != null)
                states[index] = state;
        }

        return b;
    }
}
