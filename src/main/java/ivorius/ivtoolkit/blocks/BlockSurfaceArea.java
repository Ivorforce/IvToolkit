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

import net.minecraft.world.gen.structure.StructureBoundingBox;

import java.util.Iterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lukas on 09.06.14.
 */
public class BlockSurfaceArea implements Iterable<BlockSurfacePos>
{
    private BlockSurfacePos point1;
    private BlockSurfacePos point2;

    public BlockSurfaceArea(BlockSurfacePos point1, BlockSurfacePos point2)
    {
        this.point1 = point1;
        this.point2 = point2;
    }

    public static BlockSurfaceArea areaFromSize(BlockSurfacePos coord, int[] size)
    {
        if (size[0] <= 0 || size[1] <= 0 || size[2] <= 0)
            throw new IllegalArgumentException();

        return new BlockSurfaceArea(coord, new BlockSurfacePos(coord.getX() + size[0] - 1, coord.getZ() + size[2] - 1));
    }

    public static BlockSurfaceArea from(BlockArea area)
    {
        return new BlockSurfaceArea(BlockSurfacePos.from(area.getPoint1()), BlockSurfacePos.from(area.getPoint2()));
    }

    public static BlockSurfaceArea from(StructureBoundingBox boundingBox)
    {
        return new BlockSurfaceArea(new BlockSurfacePos(boundingBox.minX, boundingBox.minZ), new BlockSurfacePos(boundingBox.maxX, boundingBox.maxZ));
    }

    public BlockSurfacePos getPoint1()
    {
        return point1;
    }

    public void setPoint1(BlockSurfacePos point1)
    {
        this.point1 = point1;
    }

    public BlockSurfacePos getPoint2()
    {
        return point2;
    }

    public void setPoint2(BlockSurfacePos point2)
    {
        this.point2 = point2;
    }

    public BlockSurfacePos getLowerCorner()
    {
        return BlockSurfacePositions.getLowerCorner(point1, point2);
    }

    public BlockSurfacePos getHigherCorner()
    {
        return BlockSurfacePositions.getHigherCorner(point1, point2);
    }

    public int[] areaSize()
    {
        BlockSurfacePos lower = getLowerCorner();
        BlockSurfacePos higher = getHigherCorner();

        return new int[]{higher.getX() - lower.getX() + 1, higher.getZ() - lower.getZ() + 1};
    }

    public boolean contains(BlockSurfacePos coord)
    {
        BlockSurfacePos lower = getLowerCorner();
        BlockSurfacePos higher = getHigherCorner();

        return coord.getX() >= lower.getX() && coord.getZ() >= lower.getZ() && coord.getX() <= higher.getX() && coord.getZ() <= higher.getZ();
    }

    @Override
    public Iterator<BlockSurfacePos> iterator()
    {
        return stream().iterator();
    }

    public Stream<BlockSurfacePos> stream()
    {
        BlockSurfacePos lower = getLowerCorner();
        BlockSurfacePos higher = getHigherCorner();
        return ivorius.ivtoolkit.util.IvStreams.flatMapToObj(IntStream.range(lower.x, higher.x + 1), x -> IntStream.range(lower.z, higher.z + 1).mapToObj(z -> new BlockSurfacePos(x, z)));
    }

    public StructureBoundingBox toStructureBoundingBox(int minY, int maxY)
    {
        BlockSurfacePos lower = getLowerCorner();
        BlockSurfacePos higher = getHigherCorner();
        return new StructureBoundingBox(lower.getX(), minY, lower.getZ(), higher.getX(), maxY, higher.getZ());
    }
}