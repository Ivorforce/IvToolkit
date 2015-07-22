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
import net.minecraft.block.Block;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IvMultiBlockHelper implements Iterable<BlockPos>
{
    private Iterator<BlockPos> iterator;
    private List<BlockPos> childLocations;
    private IvTileEntityMultiBlock parentTileEntity = null;

    private World world;
    private IBlockState blockState;

    private EnumFacing direction;
    private double[] center;
    private double[] size;

    public IvMultiBlockHelper()
    {

    }

    public boolean beginPlacing(List<BlockPos> positions, World world, BlockPos pos, EnumFacing blockSide, ItemStack itemStack, EntityPlayer player, IBlockState state, EnumFacing direction)
    {
        List<BlockPos> validLocations = IvMultiBlockHelper.getBestPlacement(positions, world, pos, blockSide, itemStack, player, state);

        if (validLocations == null)
            return false;

        return beginPlacing(validLocations, world, state, direction);
    }

    public boolean beginPlacing(List<BlockPos> validLocations, World world, IBlockState state, EnumFacing direction)
    {
        this.world = world;
        this.parentTileEntity = null;
        this.direction = direction;

        this.blockState = state;
        this.center = IvMultiBlockHelper.getTileEntityCenter(validLocations);
        this.size = IvMultiBlockHelper.getTileEntitySize(validLocations);
        this.childLocations = validLocations;

        this.iterator = validLocations.iterator();

        return true;
    }

    @Override
    public Iterator<BlockPos> iterator()
    {
        return iterator;
    }

    public IvTileEntityMultiBlock placeBlock(BlockPos pos)
    {
        return placeBlock(pos, this.parentTileEntity == null);
    }

    private IvTileEntityMultiBlock placeBlock(BlockPos pos, boolean parent)
    {
        world.setBlockState(pos, blockState, 3);
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof IvTileEntityMultiBlock)
        {
            IvTileEntityMultiBlock tileEntityMB = (IvTileEntityMultiBlock) tileEntity;

            if (parent)
            {
                parentTileEntity = tileEntityMB;
                childLocations.remove(parentTileEntity.getPos());
                parentTileEntity.becomeParent(childLocations);
            }
            else
            {
                tileEntityMB.becomeChild(parentTileEntity);
            }

            tileEntityMB.facing = direction;
            tileEntityMB.centerCoords = new double[]{center[0] - pos.getX(), center[1] - pos.getY(), center[2] - pos.getZ()};
            tileEntityMB.centerCoordsSize = size;

            return tileEntityMB;
        }

        return null;
    }

    public static double[] getTileEntityCenter(List<BlockPos> positions)
    {
        double[] result = getCenter(positions);

        return new double[]{result[0] + 0.5f, result[1] + 0.5f, result[2] + 0.5f};
    }

    public static double[] getTileEntitySize(List<BlockPos> positions)
    {
        return getSize(positions);
    }

    public static double[] getCenter(List<BlockPos> positions)
    {
        if (positions.size() > 0)
        {
            BlockPos min = BlockPositions.getLowerCorner(positions);
            BlockPos max = BlockPositions.getHigherCorner(positions);

            double[] result = new double[3];
            result[0] = (min.getX() + max.getX()) * 0.5;
            result[1] = (min.getY() + max.getY()) * 0.5;
            result[2] = (min.getZ() + max.getZ()) * 0.5;

            return result;
        }

        return null;
    }

    public static double[] getSize(List<BlockPos> positions)
    {
        if (positions.size() > 0)
        {
            BlockPos min = BlockPositions.getLowerCorner(positions);
            BlockPos max = BlockPositions.getHigherCorner(positions);

            double[] result = new double[3];
            result[0] = (min.getX() - max.getX() + 1) * 0.5;
            result[1] = (min.getY() - max.getY() + 1) * 0.5;
            result[2] = (min.getZ() - max.getZ() + 1) * 0.5;

            return result;
        }

        return null;
    }

    public static int[] getLengths(List<BlockPos> positions)
    {
        BlockPos min = BlockPositions.getLowerCorner(positions);
        BlockPos max = BlockPositions.getHigherCorner(positions);

        return new int[]{max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ()};
    }

    public static boolean canPlace(World world, IBlockState state, List<BlockPos> positions, Entity entity, ItemStack stack)
    {
        for (BlockPos position : positions)
        {
            if (!world.canBlockBePlaced(state.getBlock(), position, false, EnumFacing.DOWN, entity, stack))
                return false;
        }

        return true;
    }

    public static List<List<BlockPos>> getValidPlacements(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state)
    {
        IBlockState var11 = world.getBlockState(pos);

        IBlockState iblockstate = world.getBlockState(pos);
        Block block = iblockstate.getBlock();

        if (block == Blocks.snow_layer && ((Integer)iblockstate.getValue(BlockSnow.LAYERS)).intValue() < 1)
        {
            side = EnumFacing.UP;
        }
        else if (!block.isReplaceable(world, pos))
        {
            pos = pos.offset(side);
        }

        if (!player.canPlayerEdit(pos, side, itemStack))
        {
            return new ArrayList<>();
        }
        else if (pos.getY() == world.getHeight() && state.getBlock().getMaterial().isSolid())
        {
            return new ArrayList<>();
        }
        else
        {
            int[] lengths = getLengths(positions);
            BlockPos min = BlockPositions.getLowerCorner(positions);

            // Run from min+length (maximimum) being the placed pos to minimum being the pos
            ArrayList<List<BlockPos>> validPlacements = new ArrayList<>();
            for (int xShift = min.getX() - lengths[0]; xShift <= min.getX(); xShift++)
            {
                for (int yShift = min.getY() - lengths[1]; yShift <= min.getY(); yShift++)
                {
                    for (int zShift = min.getZ() - lengths[2]; zShift <= min.getZ(); zShift++)
                    {
                        ArrayList<BlockPos> validPositions = new ArrayList<>();

                        for (BlockPos position : positions)
                            validPositions.add(position.add(xShift, yShift, zShift).add(pos));

                        if (canPlace(world, state, validPositions, null, itemStack))
                            validPlacements.add(validPositions);
                    }
                }
            }

            return validPlacements;
        }
    }

    public static List<BlockPos> getBestPlacement(List<BlockPos> positions, World world, BlockPos pos, EnumFacing side, ItemStack itemStack, EntityPlayer player, IBlockState state)
    {
        int[] lengths = getLengths(positions);

        List<List<BlockPos>> validPlacements = getValidPlacements(positions, world, pos, side, itemStack, player, state);

        if (validPlacements.size() > 0)
        {
            float[] center = new float[]{pos.getX() - lengths[0] * 0.5f, pos.getY() - lengths[1] * 0.5f, pos.getZ() - lengths[2] * 0.5f};
            List<BlockPos> preferredPositions = validPlacements.get(0);
            for (int i = 1; i < validPlacements.size(); i++)
            {
                BlockPos referenceBlock = validPlacements.get(i).get(0);
                BlockPos referenceBlockOriginal = preferredPositions.get(0);

                if (distanceSquared(BlockPositions.toIntArray(referenceBlock), center) < distanceSquared(BlockPositions.toIntArray(referenceBlockOriginal), center))
                    preferredPositions = validPlacements.get(i);
            }

            return preferredPositions;
        }

        return null;
    }

    private static float distanceSquared(int[] referenceBlock, float[] center)
    {
        float distX = referenceBlock[0] - center[0];
        float distY = referenceBlock[1] - center[1];
        float distZ = referenceBlock[2] - center[2];

        return distX * distX + distY * distY + distZ * distZ;
    }

    public static List<BlockPos> getRotatedPositions(List<BlockPos> positions, EnumFacing facing)
    {
        ArrayList<BlockPos> returnList = new ArrayList<>(positions.size());

        for (BlockPos position : positions)
        {
            switch (facing)
            {
                case NORTH:
                case SOUTH:
                    returnList.add(position);
                    break;
                case WEST:
                case EAST:
                    returnList.add(new BlockPos(position.getZ(), position.getY(), position.getX()));
                    break;
            }
        }

        return returnList;
    }

    public static IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords)
    {
        IvRaytraceableAxisAlignedBox box = null;

        switch (direction)
        {
            case SOUTH:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
                break;
            case WEST:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
                break;
            case NORTH:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
                break;
            case EAST:
                box = new IvRaytraceableAxisAlignedBox(userInfo, centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
                break;
        }

        return box;
    }

    public static AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords)
    {
        AxisAlignedBB box = null;

        switch (direction)
        {
            case SOUTH:
                box = getBBWithLengths(centerCoords[0] + x, centerCoords[1] + y, centerCoords[2] + z, width, height, depth);
                break;
            case WEST:
                box = getBBWithLengths(centerCoords[0] - z - depth, centerCoords[1] + y, centerCoords[2] + x, depth, height, width);
                break;
            case NORTH:
                box = getBBWithLengths(centerCoords[0] - x - width, centerCoords[1] + y, centerCoords[2] - z - depth, width, height, depth);
                break;
            case EAST:
                box = getBBWithLengths(centerCoords[0] + z, centerCoords[1] + y, centerCoords[2] - x - width, depth, height, width);
                break;
        }

        return box;
    }

    public static Vector3f getRotatedVector(Vector3f vector3f, EnumFacing facing)
    {
        switch (facing)
        {
            case SOUTH:
                return new Vector3f(vector3f.x, vector3f.y, vector3f.z);
            case WEST:
                return new Vector3f(-vector3f.z, vector3f.y, vector3f.x);
            case NORTH:
                return new Vector3f(-vector3f.x, vector3f.y, -vector3f.z);
            case EAST:
                return new Vector3f(vector3f.z, vector3f.y, -vector3f.x);
        }

        return null;
    }

    public static Vec3 getRotatedVector(Vec3 vec3, EnumFacing facing)
    {
        switch (facing)
        {
            case SOUTH:
                return new Vec3(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            case WEST:
                return new Vec3(-vec3.zCoord, vec3.yCoord, vec3.xCoord);
            case NORTH:
                return new Vec3(-vec3.xCoord, vec3.yCoord, -vec3.zCoord);
            case EAST:
                return new Vec3(vec3.zCoord, vec3.yCoord, -vec3.xCoord);
        }

        return null;
    }

    public static AxisAlignedBB getBBWithLengths(double x, double y, double z, double width, double height, double depth)
    {
        return AxisAlignedBB.fromBounds(x, y, z, x + width, y + height, z + depth);
    }

    public static EnumFacing getRotation(Entity entity)
    {
        return entity.getHorizontalFacing();
    }

    public static List<BlockPos> getRotatedPositions(EnumFacing facing, int width, int height, int length)
    {
        boolean affectsX = (facing == EnumFacing.SOUTH) || (facing == EnumFacing.NORTH);
        return getPositions(affectsX ? width : length, height, affectsX ? length : width);
    }

    public static List<BlockPos> getPositions(int width, int height, int length)
    {
        ArrayList<BlockPos> positions = new ArrayList<>();

        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                for (int z = 0; z < length; z++)
                {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }
}
