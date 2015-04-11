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
    public static final AxisAlignedTransform2D ORIGINAL = new AxisAlignedTransform2D(0, false);

    private final int rotation;
    private final boolean mirrorX;

    public AxisAlignedTransform2D(int rotation, boolean mirrorX)
    {
        this.rotation = ((rotation % 4) + 4) % 4;
        this.mirrorX = mirrorX;
    }

    public static AxisAlignedTransform2D transform(int rotationClockwise, boolean flipX)
    {
        return flipX ? ORIGINAL.rotateClockwise(rotationClockwise).flipX() : ORIGINAL.rotateClockwise(rotationClockwise);
    }

    public static AxisAlignedTransform2D transform(AxisAlignedTransform2D original, int rotationClockwise, boolean flipX)
    {
        return flipX ? original.rotateClockwise(rotationClockwise).flipX() : original.rotateClockwise(rotationClockwise);
    }

    public AxisAlignedTransform2D rotateClockwise(int steps)
    {
        return new AxisAlignedTransform2D(rotation + steps, mirrorX);
    }

    public int getRotation()
    {
        return rotation;
    }

    public boolean isMirrorX()
    {
        return mirrorX;
    }

    public AxisAlignedTransform2D rotateClockwise()
    {
        return new AxisAlignedTransform2D(rotation + 1, mirrorX);
    }

    public AxisAlignedTransform2D rotateCounterClockwise(int steps)
    {
        return new AxisAlignedTransform2D(rotation - steps, mirrorX);
    }

    public AxisAlignedTransform2D rotateCounterClockwise()
    {
        return new AxisAlignedTransform2D(rotation - 1, mirrorX);
    }

    public AxisAlignedTransform2D flipX()
    {
        return new AxisAlignedTransform2D(rotation, !mirrorX);
    }

    public AxisAlignedTransform2D flipZ()
    {
        return new AxisAlignedTransform2D(rotation + 2, !mirrorX);
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
}
