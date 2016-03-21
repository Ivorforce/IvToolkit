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

import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.raytracing.IvRaytraceableObject;
import ivorius.ivtoolkit.raytracing.IvRaytracedIntersection;
import ivorius.ivtoolkit.raytracing.IvRaytracer;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Created by lukas on 11.02.14.
 */
public class IvBlockCollection implements Iterable<BlockCoord>
{
    private final Block[] blocks;
    private final byte[] metas;
    public final int width;
    public final int height;
    public final int length;

    public IvBlockCollection(int width, int height, int length)
    {
        this(airArray(width, height, length), new byte[width * height * length], width, height, length);
    }

    private static Block[] airArray(int width, int height, int length)
    {
        Block[] blocks = new Block[width * height * length];
        Arrays.fill(blocks, Blocks.air);
        return blocks;
    }

    public IvBlockCollection(Block[] blocks, byte[] metas, int width, int height, int length)
    {
        if (blocks.length != width * height * length || metas.length != blocks.length)
            throw new IllegalArgumentException();

        this.blocks = blocks;
        this.metas = metas;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public IvBlockCollection(NBTTagCompound compound, MCRegistry registry)
    {
        width = compound.getInteger("width");
        height = compound.getInteger("height");
        length = compound.getInteger("length");

        metas = compound.getByteArray("metadata");

        IvBlockMapper mapper = new IvBlockMapper(compound, "mapping", registry);
        blocks = mapper.createBlocksFromNBT(compound.getCompoundTag("blocks"));
        if (blocks.length != width * height * length)
        {
            throw new RuntimeException("Block collection length is " + blocks.length + " but should be " + width + " * " + height + " * " + length);
        }
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

    public Block getBlock(BlockCoord coord)
    {
        if (!hasCoord(coord))
            return Blocks.air;

        Block block = blocks[indexFromCoord(coord)];
        return block != null ? block : Blocks.air;
    }

    public byte getMetadata(BlockCoord coord)
    {
        if (!hasCoord(coord))
            return 0;

        return metas[indexFromCoord(coord)];
    }

    public void setBlockAndMetadata(BlockCoord coord, Block block, byte meta)
    {
        if (block == null)
            throw new NullPointerException();

        if (!hasCoord(coord))
            return;

        int index = indexFromCoord(coord);
        blocks[index] = block;
        metas[index] = meta;
    }

    public void setBlock(BlockCoord coord, Block block)
    {
        if (block == null)
            throw new NullPointerException();

        if (!hasCoord(coord))
            return;

        blocks[indexFromCoord(coord)] = block;
    }

    public void setMetadata(BlockCoord coord, byte meta)
    {
        if (!hasCoord(coord))
            return;

        metas[indexFromCoord(coord)] = meta;
    }

    private int indexFromCoord(BlockCoord coord)
    {
        return ((coord.z * height) + coord.y) * width + coord.x;
    }

    public boolean hasCoord(BlockCoord coord)
    {
        return coord.x >= 0 && coord.x < width && coord.y >= 0 && coord.y < height && coord.z >= 0 && coord.z < length;
    }

    public boolean shouldRenderSide(BlockCoord coord, ForgeDirection side)
    {
        BlockCoord sideCoord = coord.add(side.offsetX, side.offsetY, side.offsetZ);

        Block block = getBlock(sideCoord);
        return !block.isOpaqueCube();
    }

    public MovingObjectPosition rayTrace(Vec3 position, Vec3 direction)
    {
        IvRaytraceableAxisAlignedBox containingBox = new IvRaytraceableAxisAlignedBox(null, 0.001, 0.001, 0.001, width - 0.002, height - 0.002, length - 0.002);
        IvRaytracedIntersection intersection = IvRaytracer.getFirstIntersection(Collections.<IvRaytraceableObject>singletonList(containingBox), position.xCoord, position.yCoord, position.zCoord, direction.xCoord, direction.yCoord, direction.zCoord);

        if (intersection != null)
        {
            position = Vec3.createVectorHelper(intersection.getX(), intersection.getY(), intersection.getZ());
            BlockCoord curCoord = new BlockCoord(MathHelper.floor_double(position.xCoord), MathHelper.floor_double(position.yCoord), MathHelper.floor_double(position.zCoord));
            ForgeDirection hitSide = ((ForgeDirection) intersection.getHitInfo()).getOpposite();

            while (hasCoord(curCoord))
            {
                if (getBlock(curCoord).getMaterial() != Material.air)
                    return new MovingObjectPosition(curCoord.x, curCoord.y, curCoord.z, hitSide.getOpposite().ordinal(), position);

                hitSide = getExitSide(position, direction);

                if (hitSide.offsetX != 0)
                {
                    double offX = hitSide.offsetX > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.x + offX) - position.xCoord) / direction.xCoord;
                    position = Vec3.createVectorHelper(curCoord.x + offX, position.yCoord + direction.yCoord * dirLength, position.zCoord + direction.zCoord * dirLength);
                }
                else if (hitSide.offsetY != 0)
                {
                    double offY = hitSide.offsetY > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.y + offY) - position.yCoord) / direction.yCoord;
                    position = Vec3.createVectorHelper(position.xCoord + direction.xCoord * dirLength, curCoord.y + offY, position.zCoord + direction.zCoord * dirLength);
                }
                else
                {
                    double offZ = hitSide.offsetZ > 0 ? 1.0001 : -0.0001;
                    double dirLength = ((curCoord.z + offZ) - position.zCoord) / direction.zCoord;
                    position = Vec3.createVectorHelper(position.xCoord + direction.xCoord * dirLength, position.yCoord + direction.yCoord * dirLength, curCoord.z + offZ);
                }

                curCoord = curCoord.add(hitSide.offsetX, hitSide.offsetY, hitSide.offsetZ);
            }
        }

        return null;
    }

    private ForgeDirection getExitSide(Vec3 position, Vec3 direction)
    {
        double innerX = ((position.xCoord % 1.0) + 1.0) % 1.0;
        double innerY = ((position.yCoord % 1.0) + 1.0) % 1.0;
        double innerZ = ((position.zCoord % 1.0) + 1.0) % 1.0;

        double xDist = direction.xCoord > 0.0 ? ((1.0 - innerX) / direction.xCoord) : (innerX / -direction.xCoord);
        double yDist = direction.yCoord > 0.0 ? ((1.0 - innerY) / direction.yCoord) : (innerY / -direction.yCoord);
        double zDist = direction.zCoord > 0.0 ? ((1.0 - innerZ) / direction.zCoord) : (innerZ / -direction.zCoord);

        if (xDist < yDist && xDist < zDist)
            return direction.xCoord > 0.0 ? ForgeDirection.EAST : ForgeDirection.WEST;
        else if (yDist < zDist)
            return direction.yCoord > 0.0 ? ForgeDirection.UP : ForgeDirection.DOWN;
        else
            return direction.zCoord > 0.0 ? ForgeDirection.SOUTH : ForgeDirection.NORTH;
    }

    public int getBlockMultiplicity()
    {
        Set<Block> blockSet = new HashSet<>();
        Collections.addAll(blockSet, blocks);
        return blockSet.size();
    }

    public NBTTagCompound createTagCompound()
    {
        NBTTagCompound compound = new NBTTagCompound();
        IvBlockMapper mapper = new IvBlockMapper();

        compound.setInteger("width", width);
        compound.setInteger("height", height);
        compound.setInteger("length", length);

        compound.setByteArray("metadata", metas);

        mapper.addMapping(blocks);
        compound.setTag("mapping", mapper.createTagList());
        compound.setTag("blocks", mapper.createNBTForBlocks(blocks));
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
        if (!Arrays.equals(blocks, that.blocks)) return false;
        if (!Arrays.equals(metas, that.metas)) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = Arrays.hashCode(blocks);
        result = 31 * result + Arrays.hashCode(metas);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + length;
        return result;
    }

    @Override
    public Iterator<BlockCoord> iterator()
    {
        return new BlockAreaIterator(BlockCoord.ZERO, new BlockCoord(width - 1, height - 1, length - 1));
    }

    public BlockArea area()
    {
        return new BlockArea(BlockCoord.ZERO, new BlockCoord(width - 1, height - 1, length - 1));
    }
}
