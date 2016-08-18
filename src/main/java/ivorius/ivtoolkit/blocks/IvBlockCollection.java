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

import com.google.common.collect.ImmutableSet;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracer;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by lukas on 11.02.14.
 */
public class IvBlockCollection
{
    public final int width;
    public final int height;
    public final int length;
    private final IBlockState[] blockStates;

    public IvBlockCollection(int width, int height, int length)
    {
        this(airArray(width, height, length), width, height, length);
    }

    public IvBlockCollection(IBlockState[] blockStates, int width, int height, int length)
    {
        this.blockStates = blockStates;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public IvBlockCollection(NBTTagCompound compound, MCRegistry registry)
    {
        width = compound.getInteger("width");
        height = compound.getInteger("height");
        length = compound.getInteger("length");

        IvBlockMapper mapper = new IvBlockMapper(compound, "mapping", registry);
        Block[] blocks = mapper.createBlocksFromNBT(compound.getCompoundTag("blocks"));
        byte[] metas = compound.getByteArray("metadata");

        if (blocks.length != width * height * length)
            throw new RuntimeException("Block collection length is " + blocks.length + " but should be " + width + " * " + height + " * " + length);
        if (metas.length != width * height * length)
            throw new RuntimeException("Block collection length is " + metas.length + " but should be " + width + " * " + height + " * " + length);

        blockStates = new IBlockState[width * height * length];
        for (int i = 0; i < blockStates.length; i++)
            blockStates[i] = blocks[i].getStateFromMeta(metas[i]);
    }

    private static IBlockState[] airArray(int width, int height, int length)
    {
        IBlockState[] blocks = new IBlockState[width * height * length];
        Arrays.fill(blocks, Blocks.AIR.getDefaultState());
        return blocks;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getLength()
    {
        return length;
    }

    public IBlockState getBlockState(BlockPos coord)
    {
        if (!hasCoord(coord))
            return Blocks.AIR.getDefaultState();

        IBlockState block = blockStates[indexFromCoord(coord)];
        return block != null ? block : Blocks.AIR.getDefaultState();
    }

    public void setBlockState(BlockPos coord, IBlockState state)
    {
        if (state == null)
            throw new NullPointerException();

        if (!hasCoord(coord))
            return;

        int index = indexFromCoord(coord);
        blockStates[index] = state;
    }

    private int indexFromCoord(BlockPos coord)
    {
        return ((coord.getZ() * height) + coord.getY()) * width + coord.getX();
    }

    public boolean hasCoord(BlockPos coord)
    {
        return coord.getX() >= 0 && coord.getX() < width && coord.getY() >= 0 && coord.getY() < height && coord.getZ() >= 0 && coord.getZ() < length;
    }

    public boolean shouldRenderSide(BlockPos coord, EnumFacing side)
    {
        BlockPos sideCoord = coord.add(side.getFrontOffsetX(), side.getFrontOffsetY(), side.getFrontOffsetZ());

        IBlockState block = getBlockState(sideCoord);
        return !block.isOpaqueCube();
    }

    public RayTraceResult rayTrace(Vec3d position, Vec3d direction)
    {
        IvRaytraceableAxisAlignedBox containingBox = new IvRaytraceableAxisAlignedBox(null, 0.001, 0.001, 0.001, width - 0.002, height - 0.002, length - 0.002);
        IvRaytracedIntersection intersection = IvRaytracer.getFirstIntersection(Collections.<IvRaytraceableObject>singletonList(containingBox), position.xCoord, position.yCoord, position.zCoord, direction.xCoord, direction.yCoord, direction.zCoord);

        if (intersection != null)
        {
            position = new Vec3d(intersection.getX(), intersection.getY(), intersection.getZ());
            BlockPos curCoord = new BlockPos(MathHelper.floor_double(position.xCoord), MathHelper.floor_double(position.yCoord), MathHelper.floor_double(position.zCoord));
            EnumFacing hitSide = ((EnumFacing) intersection.getHitInfo()).getOpposite();

            while (hasCoord(curCoord))
            {
                if (getBlockState(curCoord).getMaterial() != Material.AIR)
                    return new RayTraceResult(position, hitSide.getOpposite(), new BlockPos(curCoord.getX(), curCoord.getY(), curCoord.getZ()));

                hitSide = getExitSide(position, direction);

                if (hitSide.getFrontOffsetX() != 0)
                {
                    double offX = hitSide.getFrontOffsetX() > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.getX() + offX) - position.xCoord) / direction.xCoord;
                    position = new Vec3d(curCoord.getX() + offX, position.yCoord + direction.yCoord * dirLength, position.zCoord + direction.zCoord * dirLength);
                }
                else if (hitSide.getFrontOffsetY() != 0)
                {
                    double offY = hitSide.getFrontOffsetY() > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.getY() + offY) - position.yCoord) / direction.yCoord;
                    position = new Vec3d(position.xCoord + direction.xCoord * dirLength, curCoord.getY() + offY, position.zCoord + direction.zCoord * dirLength);
                }
                else
                {
                    double offZ = hitSide.getFrontOffsetZ() > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.getZ() + offZ) - position.zCoord) / direction.zCoord;
                    position = new Vec3d(position.xCoord + direction.xCoord * dirLength, position.yCoord + direction.yCoord * dirLength, curCoord.getZ() + offZ);
                }

                curCoord = curCoord.add(hitSide.getFrontOffsetX(), hitSide.getFrontOffsetY(), hitSide.getFrontOffsetZ());
            }
        }

        return null;
    }

    private EnumFacing getExitSide(Vec3d position, Vec3d direction)
    {
        double innerX = ((position.xCoord % 1.0) + 1.0) % 1.0;
        double innerY = ((position.yCoord % 1.0) + 1.0) % 1.0;
        double innerZ = ((position.zCoord % 1.0) + 1.0) % 1.0;

        double xDist = direction.xCoord > 0.0 ? ((1.0 - innerX) / direction.xCoord) : (innerX / -direction.xCoord);
        double yDist = direction.yCoord > 0.0 ? ((1.0 - innerY) / direction.yCoord) : (innerY / -direction.yCoord);
        double zDist = direction.zCoord > 0.0 ? ((1.0 - innerZ) / direction.zCoord) : (innerZ / -direction.zCoord);

        if (xDist < yDist && xDist < zDist)
            return direction.xCoord > 0.0 ? EnumFacing.EAST : EnumFacing.WEST;
        else if (yDist < zDist)
            return direction.yCoord > 0.0 ? EnumFacing.UP : EnumFacing.DOWN;
        else
            return direction.zCoord > 0.0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
    }

    public int getBlockMultiplicity()
    {
        return new ImmutableSet.Builder<>().addAll(Arrays.asList(blockStates)).build().size();
    }

    public NBTTagCompound createTagCompound()
    {
        NBTTagCompound compound = new NBTTagCompound();
        IvBlockMapper mapper = new IvBlockMapper();

        compound.setInteger("width", width);
        compound.setInteger("height", height);
        compound.setInteger("length", length);

        byte[] metas = new byte[blockStates.length];
        for (int i = 0; i < metas.length; i++)
            metas[i] = (byte) blockStates[i].getBlock().getMetaFromState(blockStates[i]);
        compound.setByteArray("metadata", metas);

        List<Block> blockList = Stream.of(blockStates).map(IBlockState::getBlock).collect(Collectors.toList());
        mapper.addMapping(blockList);
        compound.setTag("mapping", mapper.createTagList());
        compound.setTag("blocks", mapper.createNBTForBlocks(blockList));
        return compound;
    }

    @Override
    public String toString()
    {
        return "IvBlockCollection{" +
                "length=" + length +
                ", height=" + height +
                ", width=" + width +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IvBlockCollection that = (IvBlockCollection) o;

        if (height != that.height) return false;
        if (length != that.length) return false;
        if (width != that.width) return false;
        if (!Arrays.equals(blockStates, that.blockStates)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(blockStates);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + length;
        return result;
    }

    public BlockArea area()
    {
        return new BlockArea(BlockPos.ORIGIN, new BlockPos(width - 1, height - 1, length - 1));
    }
}
