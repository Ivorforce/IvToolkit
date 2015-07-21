/*
 * Copyright 2014 Lukas Tenbrink
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

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.Iterator;

/**
 * Created by lukas on 09.06.14.
 */
public class BlockArea implements Iterable<BlockPos>
{
    private BlockPos point1;
    private BlockPos point2;

    public BlockArea(BlockPos point1, BlockPos point2)
    {
        this.point1 = point1;
        this.point2 = point2;
    }

    public static BlockArea areaFromSize(BlockPos coord, int[] size)
    {
        if (size[0] <= 0 || size[1] <= 0 || size[2] <= 0)
            throw new IllegalArgumentException();

        return new BlockArea(coord, new BlockPos(coord.getX() + size[0] - 1, coord.getY() + size[1] - 1, coord.getZ() + size[2] - 1));
    }

    public BlockPos getPoint1()
    {
        return point1;
    }

    public void setPoint1(BlockPos point1)
    {
        this.point1 = point1;
    }

    public BlockPos getPoint2()
    {
        return point2;
    }

    public void setPoint2(BlockPos point2)
    {
        this.point2 = point2;
    }

    public BlockPos getLowerCorner()
    {
        return BlockPositions.getLowerCorner(point1, point2);
    }

    public BlockPos getHigherCorner()
    {
        return BlockPositions.getHigherCorner(point1, point2);
    }

    public int[] areaSize()
    {
        BlockPos lower = getLowerCorner();
        BlockPos higher = getHigherCorner();

        return new int[]{higher.getX() - lower.getX() + 1, higher.getY() - lower.getY() + 1, higher.getZ() - lower.getZ() + 1};
    }

    public boolean contains(BlockPos coord)
    {
        BlockPos lower = getLowerCorner();
        BlockPos higher = getHigherCorner();

        return coord.getX() >= lower.getX() && coord.getY() >= lower.getY() && coord.getZ() >= lower.getZ() && coord.getX() <= higher.getX() && coord.getY() <= higher.getY() && coord.getZ() <= higher.getZ();
    }

    public AxisAlignedBB asAxisAlignedBB()
    {
        BlockPos lower = getLowerCorner();
        BlockPos higher = getHigherCorner();

        return AxisAlignedBB.fromBounds(lower.getX(), lower.getY(), lower.getZ(), higher.getX(), higher.getY(), higher.getZ());
    }

    @Override
    public Iterator<BlockPos> iterator()
    {
        return new BlockAreaIterator(getLowerCorner(), getHigherCorner());
    }
}
