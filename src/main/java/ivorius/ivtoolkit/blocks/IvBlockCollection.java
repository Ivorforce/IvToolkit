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
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Arrays;

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
        if (blockStates.length != width * height * length)
            throw new IllegalArgumentException();

        this.blockStates = blockStates.clone();
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public IvBlockCollection(NBTTagCompound compound, MCRegistry registry)
    {
        width = compound.getInt("width");
        height = compound.getInt("height");
        length = compound.getInt("length");

        if (compound.hasKey("blocks")) {
            // Legacy

            IvBlockMapper mapper = new IvBlockMapper(compound, "mapping", registry);
            String[] blocks = mapper.createBlocksFromNBT(compound.getCompound("blocks"));
            byte[] metas = compound.getByteArray("metadata");

            if (blocks.length != width * height * length)
                throw new RuntimeException("Block collection length is " + blocks.length + " but should be " + width + " * " + height + " * " + length);
            if (metas.length != width * height * length)
                throw new RuntimeException("Block collection length is " + metas.length + " but should be " + width + " * " + height + " * " + length);

            blockStates = new IBlockState[width * height * length];
            for (int i = 0; i < blockStates.length; i++)
                blockStates[i] = BlockStates.fromLegacyMetadata(blocks[i], metas[i]);

            return;
        }

        IvBlockStateMapper mapper = new IvBlockStateMapper(compound, "mapping", registry);
        blockStates = mapper.createStatesFromNBT(compound.getCompound("states"));

        if (blockStates.length != width * height * length)
            throw new RuntimeException("Block collection length is " + blockStates.length + " but should be " + width + " * " + height + " * " + length);
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
        BlockPos sideCoord = coord.add(side.getXOffset(), side.getYOffset(), side.getZOffset());

        IBlockState block = getBlockState(sideCoord);
        return !block.isSideInvisible(block, side);
    }

//    public RayTraceResult rayTrace(Vec3d position, Vec3d direction)
//    {
//        IvRaytraceableAxisAlignedBox containingBox = new IvRaytraceableAxisAlignedBox(null, 0.001, 0.001, 0.001, width - 0.002, height - 0.002, length - 0.002);
//        IvRaytracedIntersection intersection = IvRaytracer.getFirstIntersection(Collections.<IvRaytraceableObject>singletonList(containingBox), position.x, position.y, position.z, direction.x, direction.y, direction.z);
//
//        if (intersection != null)
//        {
//            position = new Vec3d(intersection.getX(), intersection.getY(), intersection.getZ());
//            BlockPos curCoord = new BlockPos(position.x, position.y, position.z);
//            EnumFacing hitSide = ((EnumFacing) intersection.getHitInfo()).getOpposite();
//
//            while (hasCoord(curCoord))
//            {
//                if (getBlockState(curCoord).getMaterial() != Material.AIR)
//                    return new RayTraceResult(position, hitSide.getOpposite(), new BlockPos(curCoord.getX(), curCoord.getY(), curCoord.getZ()));
//
//                hitSide = getExitSide(position, direction);
//
//                if (hitSide.getXOffset() != 0)
//                {
//                    double offX = hitSide.getXOffset() > 0 ? 1.0001 : -0.0001;
//                    double dirLength = ((curCoord.getX() + offX) - position.x) / direction.x;
//                    position = new Vec3d(curCoord.getX() + offX, position.y + direction.y * dirLength, position.z + direction.z * dirLength);
//                }
//                else if (hitSide.getYOffset() != 0)
//                {
//                    double offY = hitSide.getYOffset() > 0 ? 1.0001 : -0.0001;
//                    double dirLength = ((curCoord.getY() + offY) - position.y) / direction.y;
//                    position = new Vec3d(position.x + direction.x * dirLength, curCoord.getY() + offY, position.z + direction.z * dirLength);
//                }
//                else
//                {
//                    double offZ = hitSide.getZOffset() > 0 ? 1.0001 : -0.0001;
//                    double dirLength = ((curCoord.getZ() + offZ) - position.z) / direction.z;
//                    position = new Vec3d(position.x + direction.x * dirLength, position.y + direction.y * dirLength, curCoord.getZ() + offZ);
//                }
//
//                curCoord = curCoord.add(hitSide.getXOffset(), hitSide.getYOffset(), hitSide.getZOffset());
//            }
//        }
//
//        return null;
//    }

    private EnumFacing getExitSide(Vec3d position, Vec3d direction)
    {
        double innerX = ((position.x % 1.0) + 1.0) % 1.0;
        double innerY = ((position.y % 1.0) + 1.0) % 1.0;
        double innerZ = ((position.z % 1.0) + 1.0) % 1.0;

        double xDist = direction.x > 0.0 ? ((1.0 - innerX) / direction.x) : (innerX / -direction.x);
        double yDist = direction.y > 0.0 ? ((1.0 - innerY) / direction.y) : (innerY / -direction.y);
        double zDist = direction.z > 0.0 ? ((1.0 - innerZ) / direction.z) : (innerZ / -direction.z);

        if (xDist < yDist && xDist < zDist)
            return direction.x > 0.0 ? EnumFacing.EAST : EnumFacing.WEST;
        else if (yDist < zDist)
            return direction.y > 0.0 ? EnumFacing.UP : EnumFacing.DOWN;
        else
            return direction.z > 0.0 ? EnumFacing.SOUTH : EnumFacing.NORTH;
    }

    public int getBlockMultiplicity()
    {
        return new ImmutableSet.Builder<>().addAll(Arrays.asList(blockStates)).build().size();
    }

    public NBTTagCompound createTagCompound(MCRegistry registry)
    {
        NBTTagCompound compound = new NBTTagCompound();
        IvBlockStateMapper mapper = new IvBlockStateMapper(registry);

        compound.setInt("width", width);
        compound.setInt("height", height);
        compound.setInt("length", length);

        ArrayList<IBlockState> states = Lists.newArrayList(blockStates);

        mapper.addMapping(states);
        compound.setTag("mapping", mapper.createTagList());
        compound.setTag("states", mapper.createNBTForStates(states));

        return compound;
    }

    public IvBlockCollection copy()
    {
        return new IvBlockCollection(blockStates, width, height, length);
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
