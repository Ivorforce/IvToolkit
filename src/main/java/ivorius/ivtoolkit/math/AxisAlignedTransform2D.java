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
        BlockCoord coord;
        int positionX = mirrorX ? size[0] - 1 - position.x : position.x;

        switch (rotation)
        {
            case 0:
                coord = new BlockCoord(positionX, position.y, position.z);
                break;
            case 1:
                coord = new BlockCoord(size[2] - 1 - position.z, position.y, positionX);
                break;
            case 2:
                coord = new BlockCoord(size[0] - 1 - positionX, position.y, size[2] - 1 - position.z);
                break;
            case 3:
                coord = new BlockCoord(position.z, position.y, size[0] - 1 - positionX);
                break;
            default:
                throw new InternalError();
        }

        return coord;
    }

    public double[] apply(double[] position, int[] size)
    {
        double[] coord;
        double positionX = mirrorX ? size[0] - 1 - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                coord = new double[]{positionX, position[1], position[2]};
                break;
            case 1:
                coord = new double[]{size[2] - 1 - position[2], position[1], positionX};
                break;
            case 2:
                coord = new double[]{size[0] - 1 - positionX, position[1], size[2] - 1 - position[2]};
                break;
            case 3:
                coord = new double[]{position[2], position[1], size[0] - 1 - positionX};
                break;
            default:
                throw new InternalError();
        }

        return coord;
    }

    public int[] apply(int[] position, int[] size)
    {
        int[] coord;
        int positionX = mirrorX ? size[0] - 1 - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                coord = new int[]{positionX, position[1], position[2]};
                break;
            case 1:
                coord = new int[]{size[2] - 1 - position[2], position[1], positionX};
                break;
            case 2:
                coord = new int[]{size[0] - 1 - positionX, position[1], size[2] - 1 - position[2]};
                break;
            case 3:
                coord = new int[]{position[2], position[1], size[0] - 1 - positionX};
                break;
            default:
                throw new InternalError();
        }

        return coord;
    }

    public void rotateBlock(World world, BlockCoord coord, Block block)
    {
        for (int i = 0; i < rotation; i++)
            block.rotateBlock(world, coord.x, coord.y, coord.z, ForgeDirection.UP);
    }
}
