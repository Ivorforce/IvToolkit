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

package ivorius.ivtoolkit.math;

import ivorius.ivtoolkit.blocks.BlockCoord;
import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by lukas on 08.06.14.
 */
public class AxisAlignedTransform2D
{
    public static final AxisAlignedTransform2D R0 = new AxisAlignedTransform2D(0, false);
    public static final AxisAlignedTransform2D R1 = new AxisAlignedTransform2D(1, false);
    public static final AxisAlignedTransform2D R2 = new AxisAlignedTransform2D(2, false);
    public static final AxisAlignedTransform2D R3 = new AxisAlignedTransform2D(3, false);

    public static final AxisAlignedTransform2D R0_F = new AxisAlignedTransform2D(0, true);
    public static final AxisAlignedTransform2D R1_F = new AxisAlignedTransform2D(1, true);
    public static final AxisAlignedTransform2D R2_F = new AxisAlignedTransform2D(2, true);
    public static final AxisAlignedTransform2D R3_F = new AxisAlignedTransform2D(3, true);

    public static final AxisAlignedTransform2D ORIGINAL = R0;

    private final int rotation;
    private final boolean mirrorX;

    @Deprecated
    public AxisAlignedTransform2D(int rotation, boolean mirrorX)
    {
        this.rotation = ((rotation % 4) + 4) % 4;
        this.mirrorX = mirrorX;
    }

    @Deprecated
    public static AxisAlignedTransform2D transform(int rotationClockwise, boolean flipX)
    {
        return from(rotationClockwise, flipX);
    }

    public static AxisAlignedTransform2D from(int rotationClockwise, boolean flipX)
    {
        switch (((rotationClockwise % 4) + 4) % 4)
        {
            case 0:
                return flipX ? R0_F : R0;
            case 1:
                return flipX ? R1_F : R1;
            case 2:
                return flipX ? R2_F : R2;
            case 3:
                return flipX ? R3_F : R3;
        }

        throw new InternalError();
    }

    public static AxisAlignedTransform2D transform(AxisAlignedTransform2D original, int rotationClockwise, boolean flipX)
    {
        return flipX ? original.rotateClockwise(rotationClockwise).flipX() : original.rotateClockwise(rotationClockwise);
    }

    public int getRotation()
    {
        return rotation;
    }

    public boolean isMirrorX()
    {
        return mirrorX;
    }

    public AxisAlignedTransform2D rotateClockwise(int steps)
    {
        return from(rotation + steps, mirrorX);
    }

    public AxisAlignedTransform2D rotateClockwise()
    {
        return from(rotation + 1, mirrorX);
    }

    public AxisAlignedTransform2D rotateCounterClockwise(int steps)
    {
        return from(rotation - steps, mirrorX);
    }

    public AxisAlignedTransform2D rotateCounterClockwise()
    {
        return from(rotation - 1, mirrorX);
    }

    public AxisAlignedTransform2D flipX()
    {
        return from(rotation, !mirrorX);
    }

    public AxisAlignedTransform2D flipZ()
    {
        return from(rotation + 2, !mirrorX);
    }

    public boolean resultSwitchesXZ()
    {
        return rotation == 1 || rotation == 3;
    }

    public boolean resultMirrorsFormerX()
    {
        return mirrorX ^ (rotation == 2 || rotation == 3);
    }

    public boolean resultMirrorsFormerZ()
    {
        return rotation == 1 || rotation == 2;
    }

    public int apply(int direction)
    {
        if (direction < 0 || direction > 3)
            throw new IllegalArgumentException();

        return (direction + rotation) % 4;
    }

    public BlockCoord apply(BlockCoord position, int[] size)
    {
        int positionX = mirrorX ? size[0] - 1 - position.x : position.x;

        switch (rotation)
        {
            case 0:
                return new BlockCoord(positionX, position.y, position.z);
            case 1:
                return new BlockCoord(size[2] - 1 - position.z, position.y, positionX);
            case 2:
                return new BlockCoord(size[0] - 1 - positionX, position.y, size[2] - 1 - position.z);
            case 3:
                return new BlockCoord(position.z, position.y, size[0] - 1 - positionX);
            default:
                throw new InternalError();
        }
    }

    public ChunkCoordinates apply(ChunkCoordinates position, int[] size)
    {
        int positionX = mirrorX ? size[0] - 1 - position.posX : position.posX;

        switch (rotation)
        {
            case 0:
                return new ChunkCoordinates(positionX, position.posY, position.posZ);
            case 1:
                return new ChunkCoordinates(size[2] - 1 - position.posZ, position.posY, positionX);
            case 2:
                return new ChunkCoordinates(size[0] - 1 - positionX, position.posY, size[2] - 1 - position.posZ);
            case 3:
                return new ChunkCoordinates(position.posZ, position.posY, size[0] - 1 - positionX);
            default:
                throw new InternalError();
        }
    }

    public double[] apply(double[] position, int[] size)
    {
        double positionX = mirrorX ? size[0] - 1 - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                return new double[]{positionX, position[1], position[2]};
            case 1:
                return new double[]{size[2] - 1 - position[2], position[1], positionX};
            case 2:
                return new double[]{size[0] - 1 - positionX, position[1], size[2] - 1 - position[2]};
            case 3:
                return new double[]{position[2], position[1], size[0] - 1 - positionX};
            default:
                throw new InternalError();
        }
    }

    public float[] apply(float[] position, int[] size)
    {
        float positionX = mirrorX ? size[0] - 1 - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                return new float[]{positionX, position[1], position[2]};
            case 1:
                return new float[]{size[2] - 1 - position[2], position[1], positionX};
            case 2:
                return new float[]{size[0] - 1 - positionX, position[1], size[2] - 1 - position[2]};
            case 3:
                return new float[]{position[2], position[1], size[0] - 1 - positionX};
            default:
                throw new InternalError();
        }
    }

    public int[] apply(int[] position, int[] size)
    {
        int positionX = mirrorX ? size[0] - 1 - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                return new int[]{positionX, position[1], position[2]};
            case 1:
                return new int[]{size[2] - 1 - position[2], position[1], positionX};
            case 2:
                return new int[]{size[0] - 1 - positionX, position[1], size[2] - 1 - position[2]};
            case 3:
                return new int[]{position[2], position[1], size[0] - 1 - positionX};
            default:
                throw new InternalError();
        }
    }

    public void rotateBlock(World world, BlockCoord coord, Block block)
    {
        for (int i = 0; i < rotation; i++)
            block.rotateBlock(world, coord.x, coord.y, coord.z, ForgeDirection.UP);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AxisAlignedTransform2D that = (AxisAlignedTransform2D) o;

        if (rotation != that.rotation) return false;
        return mirrorX == that.mirrorX;

    }

    @Override
    public int hashCode()
    {
        int result = rotation;
        result = 31 * result + (mirrorX ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "AxisAlignedTransform2D{" +
                "rotation=" + rotation +
                ", mirrorX=" + mirrorX +
                '}';
    }
}
