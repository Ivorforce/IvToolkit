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

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;

/**
 * Created by lukas on 21.07.15.
 */
public final class BlockPositions
{
    private BlockPositions()
    {
    }

    public static BlockPos fromIntArray(int[] array)
    {
        if (array.length != 3)
            throw new IllegalArgumentException();

        return new BlockPos(array[0], array[1], array[2]);
    }

    public static int[] toIntArray(BlockPos pos)
    {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

    public static BlockPos readWithBase(NBTTagCompound compound, String keyBase)
    {
        return new BlockPos(compound.getInteger(keyBase + "_x"), compound.getInteger(keyBase + "_y"), compound.getInteger(keyBase + "_z"));
    }

    public static void writeToNBT(String keyBase, BlockPos coord, NBTTagCompound compound)
    {
        if (coord != null)
        {
            compound.setInteger(keyBase + "_x", coord.getX());
            compound.setInteger(keyBase + "_y", coord.getY());
            compound.setInteger(keyBase + "_z", coord.getZ());
        }
    }

    public static BlockPos readFromNBT(String keyBase, NBTTagCompound compound)
    {
        return compound.hasKey(keyBase + "_x") && compound.hasKey(keyBase + "_y") && compound.hasKey(keyBase + "_z")
                ? new BlockPos(compound.getInteger(keyBase + "_x"), compound.getInteger(keyBase + "_y"), compound.getInteger(keyBase + "_z"))
                : null;

    }

    public static void maybeWriteToBuffer(BlockPos coord, ByteBuf buffer)
    {
        buffer.writeBoolean(coord != null);

        if (coord != null)
            writeToBuffer(coord, buffer);
    }

    public static BlockPos maybeReadFromBuffer(ByteBuf buffer)
    {
        return buffer.readBoolean() ? readFromBuffer(buffer) : null;
    }

    public static void writeToBuffer(BlockPos coord, ByteBuf buffer)
    {
        buffer.writeInt(coord.getX());
        buffer.writeInt(coord.getY());
        buffer.writeInt(coord.getZ());
    }

    public static BlockPos readFromBuffer(ByteBuf buffer)
    {
        return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static BlockPos getLowerCorner(Collection<BlockPos> positions)
    {
        int x = 0, y = 0, z = 0;
        boolean first = true;

        for (BlockPos position : positions)
        {
            if (first)
            {
                x = position.getX();
                y = position.getY();
                z = position.getZ();
                first = false;
            }

            x = Math.min(x, position.getX());
            y = Math.min(y, position.getY());
            z = Math.min(z, position.getZ());
        }

        if (first)
            throw new ArrayIndexOutOfBoundsException();

        return new BlockPos(x, y, z);
    }

    public static BlockPos getHigherCorner(Collection<BlockPos> positions)
    {
        int x = 0, y = 0, z = 0;
        boolean first = true;

        for (BlockPos position : positions)
        {
            if (first)
            {
                x = position.getX();
                y = position.getY();
                z = position.getZ();
                first = false;
            }

            x = Math.max(x, position.getX());
            y = Math.max(y, position.getY());
            z = Math.max(z, position.getZ());
        }

        if (first)
            throw new ArrayIndexOutOfBoundsException();

        return new BlockPos(x, y, z);
    }

    public static BlockPos getLowerCorner(BlockPos one, BlockPos two)
    {
        return new BlockPos(Math.min(one.getX(), two.getX()), Math.min(one.getY(), two.getY()), Math.min(one.getZ(), two.getZ()));
    }

    public static BlockPos getHigherCorner(BlockPos one, BlockPos two)
    {
        return new BlockPos(Math.max(one.getX(), two.getX()), Math.max(one.getY(), two.getY()), Math.max(one.getZ(), two.getZ()));
    }

    public static BlockPos invert(BlockPos pos)
    {
        return new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
    }

    public static BlockPos sub(BlockPos pos, BlockPos sub)
    {
        return new BlockPos(pos.getX() - sub.getX(), pos.getY() - sub.getY(), pos.getZ() - sub.getZ());
    }

    @Deprecated
    public static AxisAlignedBB expandToAABB(BlockPos pos, double x, double y, double z)
    {
        return new AxisAlignedBB(pos).expand(x, y, z);
    }
}
