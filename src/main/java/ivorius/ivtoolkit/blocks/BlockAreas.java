/*
 * Copyright 2015 Lukas Tenbrink
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

import ivorius.ivtoolkit.gui.IntegerRange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by lukas on 28.03.15.
 */
public class BlockAreas
{
    public static int sideLength(BlockArea area, EnumFacing side)
    {
        int[] size = area.areaSize();
        switch (side)
        {
            case EAST:
            case WEST:
                return size[0];
            case UP:
            case DOWN:
                return size[1];
            case NORTH:
            case SOUTH:
                return size[2];
        }

        throw new IllegalArgumentException();
    }

    public static BlockArea side(BlockArea area, EnumFacing side)
    {
        BlockPos lowerCorner = area.getLowerCorner();
        BlockPos higherCorner = area.getHigherCorner();

        switch (side)
        {
            case UP:
                return new BlockArea(new BlockPos(lowerCorner.getX(), higherCorner.getY(), lowerCorner.getZ()), higherCorner);
            case DOWN:
                return new BlockArea(lowerCorner, new BlockPos(higherCorner.getX(), lowerCorner.getY(), higherCorner.getZ()));
            case NORTH:
                return new BlockArea(lowerCorner, new BlockPos(higherCorner.getX(), higherCorner.getY(), lowerCorner.getZ()));
            case EAST:
                return new BlockArea(new BlockPos(higherCorner.getX(), lowerCorner.getY(), lowerCorner.getZ()), higherCorner);
            case SOUTH:
                return new BlockArea(new BlockPos(lowerCorner.getX(), lowerCorner.getY(), higherCorner.getZ()), higherCorner);
            case WEST:
                return new BlockArea(lowerCorner, new BlockPos(lowerCorner.getX(), higherCorner.getY(), higherCorner.getZ()));
            default:
                throw new IllegalArgumentException();
        }
    }

    @Nullable
    public static BlockArea shrink(BlockArea area, EnumFacing side, int amount)
    {
        switch (side)
        {
            case UP:
                return shrink(area, BlockPos.ORIGIN, new BlockPos(0, amount, 0));
            case DOWN:
                return shrink(area, new BlockPos(0, amount, 0), BlockPos.ORIGIN);
            case NORTH:
                return shrink(area, new BlockPos(0, 0, amount), BlockPos.ORIGIN);
            case EAST:
                return shrink(area, BlockPos.ORIGIN, new BlockPos(amount, 0, 0));
            case SOUTH:
                return shrink(area, BlockPos.ORIGIN, new BlockPos(0, 0, amount));
            case WEST:
                return shrink(area, new BlockPos(amount, 0, 0), BlockPos.ORIGIN);
            default:
                throw new IllegalArgumentException();
        }
    }

    @Nullable
    public static BlockArea shrink(BlockArea area, BlockPos lower, BlockPos higher)
    {
        BlockPos p1 = area.getPoint1();
        BlockPos p2 = area.getPoint2();
        IntegerRange x = shrink(p1.getX(), p2.getX(), lower.getX(), higher.getX());
        IntegerRange y = shrink(p1.getY(), p2.getY(), lower.getY(), higher.getY());
        IntegerRange z = shrink(p1.getZ(), p2.getZ(), lower.getZ(), higher.getZ());

        return x != null && y != null && z != null
                ? new BlockArea(new BlockPos(x.min, y.min, z.min), new BlockPos(x.max, y.max, z.max))
                : null;
    }

    @Nullable
    private static IntegerRange shrink(int l, int r, int shrMin, int shrMax)
    {
        boolean c = l < r;
        return Math.abs(l - r) >= shrMin + shrMax
                ? new IntegerRange(c ? l + shrMin : l - shrMax, c ? r - shrMax : r + shrMin)
                : null;
    }

    @Nonnull
    public static BlockArea expand(BlockArea area, BlockPos lower, BlockPos higher)
    {
        BlockPos p1 = area.getPoint1();
        BlockPos p2 = area.getPoint2();
        IntegerRange x = expand(p1.getX(), p2.getX(), lower.getX(), higher.getX());
        IntegerRange y = expand(p1.getY(), p2.getY(), lower.getY(), higher.getY());
        IntegerRange z = expand(p1.getZ(), p2.getZ(), lower.getZ(), higher.getZ());

        return new BlockArea(new BlockPos(x.min, y.min, z.min), new BlockPos(x.max, y.max, z.max));
    }

    @Nonnull
    private static IntegerRange expand(int l, int r, int expMin, int expMax)
    {
        boolean c = l < r;
        return new IntegerRange(c ? l - expMin : l + expMax, c ? r + expMax : r - expMin);
    }

    @Nonnull
    public static BlockArea expand(BlockArea area, EnumFacing side, int amount)
    {
        switch (side)
        {
            case UP:
                return BlockAreas.expand(area, BlockPos.ORIGIN, new BlockPos(0, amount, 0));
            case DOWN:
                return BlockAreas.expand(area, new BlockPos(0, amount, 0), BlockPos.ORIGIN);
            case NORTH:
                return BlockAreas.expand(area, new BlockPos(0, 0, amount), BlockPos.ORIGIN);
            case EAST:
                return BlockAreas.expand(area, BlockPos.ORIGIN, new BlockPos(amount, 0, 0));
            case SOUTH:
                return BlockAreas.expand(area, BlockPos.ORIGIN, new BlockPos(0, 0, amount));
            case WEST:
                return BlockAreas.expand(area, new BlockPos(amount, 0, 0), BlockPos.ORIGIN);
            default:
                throw new IllegalArgumentException();
        }
    }

    public static Iterable<BlockPos> positions(BlockArea area)
    {
        return BlockPos.getAllInBox(area.getPoint1(), area.getPoint2());
    }

    public static Iterable<BlockPos.MutableBlockPos> mutablePositions(BlockArea area)
    {
        return BlockPos.getAllInBoxMutable(area.getPoint1(), area.getPoint2());
    }

    public static Stream<BlockPos> streamPositions(BlockArea area)
    {
        return StreamSupport.stream(BlockPos.getAllInBox(area.getPoint1(), area.getPoint2()).spliterator(), false);
    }

    public static Stream<BlockPos.MutableBlockPos> streamMutablePositions(BlockArea area)
    {
        return StreamSupport.stream(BlockPos.getAllInBoxMutable(area.getPoint1(), area.getPoint2()).spliterator(), false);
    }

    public static MutableBoundingBox toBoundingBox(BlockArea area)
    {
        return new MutableBoundingBox(area.getLowerCorner(), area.getHigherCorner());
    }
}
