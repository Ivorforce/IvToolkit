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

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

/**
 * Created by lukas on 10.08.16.
 */
public class BlockSurfacePos
{
    public static final BlockSurfacePos ORIGIN = new BlockSurfacePos(0, 0);

    /**
     * The X position of this Chunk Coordinate Pair
     */
    public final int x;
    /**
     * The Z position of this Chunk Coordinate Pair
     */
    public final int z;

    public BlockSurfacePos(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public static BlockSurfacePos from(ChunkPos chunkCoords, int x, int z)
    {
        return new BlockSurfacePos(chunkCoords.chunkXPos << 4 + x, chunkCoords.chunkZPos << 4 + z);
    }

    public static BlockSurfacePos from(BlockPos pos)
    {
        return new BlockSurfacePos(pos.getX(), pos.getZ());
    }

    public ChunkPos chunkCoord()
    {
        return new ChunkPos(x >> 4, z >> 4);
    }

    public BlockPos blockPos(int y)
    {
        return new BlockPos(x, y, z );
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }

    public static long toLong(int x, int z)
    {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockSurfacePos that = (BlockSurfacePos) o;

        if (x != that.x) return false;
        return z == that.z;

    }

    @Override
    public int hashCode()
    {
        int result = x;
        result = 31 * result + z;
        return result;
    }

    public String toString()
    {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockSurfacePos add(int x, int z)
    {
        return new BlockSurfacePos(this.x + x, this.z + z);
    }

    public BlockSurfacePos subtract(int x, int z)
    {
        return new BlockSurfacePos(this.x - x, this.z - z);
    }
}
