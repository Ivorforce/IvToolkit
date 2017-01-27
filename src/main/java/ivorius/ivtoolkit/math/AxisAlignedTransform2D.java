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

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

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
    public AxisAlignedTransform2D(int rotationClockwise, boolean mirrorX)
    {
        this.rotation = ((rotationClockwise % 4) + 4) % 4;
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

        boolean mirrorApplies = mirrorX && (rotation % 2) == 1; // Mirror is applied first, so it has to be left or right to apply
        return (direction + rotation + (mirrorApplies ? 2 : 0)) % 4;
    }

    public EnumFacing apply(EnumFacing facing)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
            return facing;

        if (mirrorX && facing.getAxis() == EnumFacing.Axis.X)
            facing = facing.getOpposite();

        return EnumFacing.HORIZONTALS[(facing.getHorizontalIndex() + rotation) % EnumFacing.HORIZONTALS.length];
    }

    public BlockPos.MutableBlockPos applyOn(BlockPos.MutableBlockPos position, int[] size)
    {
        return applyOn(position, position, size, 1);
    }

    public BlockPos.MutableBlockPos applyOn(BlockPos.MutableBlockPos position, int[] size, int centerCorrection)
    {
        return applyOn(position, position, size, centerCorrection);
    }

    public BlockPos.MutableBlockPos applyOn(BlockPos position, BlockPos.MutableBlockPos onPosition, int[] size)
    {
        return applyOn(position, onPosition, size, 1);
    }

    public BlockPos.MutableBlockPos applyOn(BlockPos position, BlockPos.MutableBlockPos onPosition, int[] size, int centerCorrection)
    {
        int positionX = mirrorX ? size[0] - centerCorrection - position.getX() : position.getX();

        switch (rotation)
        {
            case 0:
                return onPosition.setPos(positionX, position.getY(), position.getZ());
            case 1:
                return onPosition.setPos(size[2] - centerCorrection - position.getZ(), position.getY(), positionX);
            case 2:
                return onPosition.setPos(size[0] - centerCorrection - positionX, position.getY(), size[2] - centerCorrection - position.getZ());
            case 3:
                return onPosition.setPos(position.getZ(), position.getY(), size[0] - centerCorrection - positionX);
            default:
                throw new InternalError();
        }
    }

    public BlockPos apply(BlockPos position, int[] size)
    {
        return apply(position, size, 1);
    }

    public BlockPos apply(BlockPos position, int[] size, int centerCorrection)
    {
        int positionX = mirrorX ? size[0] - centerCorrection - position.getX() : position.getX();

        switch (rotation)
        {
            case 0:
                return new BlockPos(positionX, position.getY(), position.getZ());
            case 1:
                return new BlockPos(size[2] - centerCorrection - position.getZ(), position.getY(), positionX);
            case 2:
                return new BlockPos(size[0] - centerCorrection - positionX, position.getY(), size[2] - centerCorrection - position.getZ());
            case 3:
                return new BlockPos(position.getZ(), position.getY(), size[0] - centerCorrection - positionX);
            default:
                throw new InternalError();
        }
    }

    public double[] apply(double[] position, int[] size)
    {
        return applyOn(position, new double[position.length], size, 0);
    }

    public double[] applyOn(double[] position, int[] size)
    {
        return applyOn(position, position, size, 0);
    }

    public double[] applyOn(double[] position, int[] size, int centerCorrection)
    {
        return applyOn(position, position, size, centerCorrection);
    }

    public double[] applyOn(double[] position, double[] on, int[] size, int centerCorrection)
    {
        double x, z;

        double positionX = mirrorX ? size[0] - centerCorrection - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                x = positionX;
                z = position[2];
                break;
            case 1:
                x = size[2] - centerCorrection - position[2];
                z = positionX;
                break;
            case 2:
                x = size[0] - centerCorrection - positionX;
                z = size[2] - centerCorrection - position[2];
                break;
            case 3:
                x = position[2];
                z = size[0] - centerCorrection - positionX;
                break;
            default:
                throw new InternalError();
        }

        on[0] = x;
        on[1] = position[1];
        on[2] = z;

        return on;
    }

    public float[] apply(float[] position, int[] size)
    {
        return applyOn(position, new float[position.length], size, 0);
    }

    public float[] applyOn(float[] position, int[] size)
    {
        return applyOn(position, position, size, 0);
    }

    public float[] applyOn(float[] position, int[] size, int centerCorrection)
    {
        return applyOn(position, position, size, centerCorrection);
    }

    public float[] applyOn(float[] position, float[] on, int[] size, int centerCorrection)
    {
        float x, z;

        float positionX = mirrorX ? size[0] - centerCorrection - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                x = positionX;
                z = position[2];
                break;
            case 1:
                x = size[2] - centerCorrection - position[2];
                z = positionX;
                break;
            case 2:
                x = size[0] - centerCorrection - positionX;
                z = size[2] - centerCorrection - position[2];
                break;
            case 3:
                x = position[2];
                z = size[0] - centerCorrection - positionX;
                break;
            default:
                throw new InternalError();
        }

        on[0] = x;
        on[1] = position[1];
        on[2] = z;

        return on;
    }

    @Deprecated
    public int[] apply(int[] position, int[] size)
    {
        return applyOn(position, new int[position.length], size, 1);
    }

    public int[] applyOn(int[] position, int[] size)
    {
        return applyOn(position, position, size, 0);
    }

    public int[] applyOn(int[] position, int[] size, int centerCorrection)
    {
        return applyOn(position, position, size, centerCorrection);
    }

    public int[] applyOn(int[] position, int[] on, int[] size, int centerCorrection)
    {
        int x, z;

        int positionX = mirrorX ? size[0] - centerCorrection - position[0] : position[0];

        switch (rotation)
        {
            case 0:
                x = positionX;
                z = position[2];
                break;
            case 1:
                x = size[2] - centerCorrection - position[2];
                z = positionX;
                break;
            case 2:
                x = size[0] - centerCorrection - positionX;
                z = size[2] - centerCorrection - position[2];
                break;
            case 3:
                x = position[2];
                z = size[0] - centerCorrection - positionX;
                break;
            default:
                throw new InternalError();
        }

        on[0] = x;
        on[1] = position[1];
        on[2] = z;

        return on;
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
