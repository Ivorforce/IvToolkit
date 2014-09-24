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

import java.util.Iterator;

/**
 * Created by lukas on 09.06.14.
 */
public class BlockArea implements Iterable<BlockCoord>
{
    private BlockCoord point1;
    private BlockCoord point2;

    public BlockArea(BlockCoord point1, BlockCoord point2)
    {
        this.point1 = point1;
        this.point2 = point2;
    }

    public static BlockArea areaFromSize(BlockCoord coord, int[] size)
    {
        if (size[0] <= 0 || size[1] <= 0 || size[2] <= 0)
            throw new IllegalArgumentException();

        return new BlockArea(coord, new BlockCoord(coord.x + size[0] - 1, coord.y + size[1] - 1, coord.z + size[2] - 1));
    }

    public BlockCoord getPoint1()
    {
        return point1;
    }

    public void setPoint1(BlockCoord point1)
    {
        this.point1 = point1;
    }

    public BlockCoord getPoint2()
    {
        return point2;
    }

    public void setPoint2(BlockCoord point2)
    {
        this.point2 = point2;
    }

    public BlockCoord getLowerCorner()
    {
        return point1.getLowerCorner(point2);
    }

    public BlockCoord getHigherCorner()
    {
        return point1.getHigherCorner(point2);
    }

    public int[] areaSize()
    {
        BlockCoord lower = getLowerCorner();
        BlockCoord higher = getHigherCorner();

        return new int[]{higher.x - lower.x + 1, higher.y - lower.y + 1, higher.z - lower.z + 1};
    }

    public boolean contains(BlockCoord coord)
    {
        BlockCoord lower = getLowerCorner();
        BlockCoord higher = getHigherCorner();

        return coord.x >= lower.x && coord.y >= lower.y && coord.z >= lower.z && coord.x <= higher.x && coord.y <= higher.y && coord.z <= higher.z;
    }

    public AxisAlignedBB asAxisAlignedBB()
    {
        BlockCoord lower = getLowerCorner();
        BlockCoord higher = getHigherCorner();

        return AxisAlignedBB.getBoundingBox(lower.x, lower.y, lower.z, higher.x, higher.y, higher.z);
    }

    @Override
    public Iterator<BlockCoord> iterator()
    {
        return new BlockAreaIterator(getLowerCorner(), getHigherCorner());
    }
}
