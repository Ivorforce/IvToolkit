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

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 14.04.17.
 */
public interface MockWorld
{
    World asWorld();

    boolean setBlockState(BlockPos pos, IBlockState state, int flags);

    IBlockState getBlockState(BlockPos pos);

    Random rand();

    default boolean setBlockState(BlockPos coord, IBlockState block)
    {
        return setBlockState(coord, block, 3);
    }

    class Real implements MockWorld
    {
        public World world;

        public Real(World world)
        {
            this.world = world;
        }

        @Override
        public World asWorld()
        {
            return world;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
        {
            return world.setBlockState(pos, state, flags);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos)
        {
            return world.getBlockState(pos);
        }

        @Override
        public Random rand()
        {
            return world.rand;
        }
    }

    class Cache implements MockWorld
    {
        public WorldCache cache;

        public Cache(WorldCache cache)
        {
            this.cache = cache;
        }

        @Override
        public World asWorld()
        {
            return cache.world;
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
        {
            return cache.setBlockState(pos, state, flags);
        }

        @Override
        public IBlockState getBlockState(BlockPos pos)
        {
            return cache.getBlockState(pos);
        }

        @Override
        public Random rand()
        {
            return cache.world.rand;
        }
    }

    class BlockCollection implements MockWorld
    {
        public IvBlockCollection collection;
        public Random random = new Random();

        public BlockCollection(IvBlockCollection collection)
        {
            this.collection = collection;
        }

        @Override
        public World asWorld()
        {
            throw new VirtualWorldException();
        }

        @Override
        public boolean setBlockState(BlockPos pos, IBlockState state, int flags)
        {
            collection.setBlockState(pos, state);

            return true;
        }

        @Override
        public IBlockState getBlockState(BlockPos pos)
        {
            return collection.getBlockState(pos);
        }

        @Override
        public Random rand()
        {
            return random;
        }
    }

    class VirtualWorldException extends RuntimeException
    {

    }
}
