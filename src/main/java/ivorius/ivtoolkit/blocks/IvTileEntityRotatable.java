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


import ivorius.ivtoolkit.tools.EnumFacingHelper;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IvTileEntityRotatable extends TileEntity
{
    public EnumFacing facing;

    public IvTileEntityRotatable(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    @Override
    public void read(NBTTagCompound tagCompound)
    {
        super.read(tagCompound);

        if (tagCompound.hasKey("direction")) // Legacy
        {
            switch (tagCompound.getInt("direction")) {
                case 0:
                    facing = EnumFacing.SOUTH;
                case 1:
                    facing = EnumFacing.WEST;
                case 2:
                    facing = EnumFacing.NORTH;
                case 3:
                    facing = EnumFacing.EAST;
                default:
                    facing = EnumFacing.SOUTH;
            }
        }
        else
            facing = EnumFacingHelper.byName(tagCompound.getString("facing"), EnumFacing.SOUTH);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tagCompound)
    {
        super.write(tagCompound);

        tagCompound.setString("facing", facing.getName());

        return tagCompound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        return IvTileEntityHelper.getStandardDescriptionPacket(this);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        read(pkt.getNbtCompound());
    }

    public AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth)
    {
        return getRotatedBB(x, y, z, width, height, depth, getFacing(), new double[]{getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5});
    }

    public Vector3f getRotatedVector(Vector3f vector3f)
    {
        return getRotatedVector(vector3f, getFacing());
    }

    public Vec3d getRotatedVector(Vec3d vec3)
    {
        return getRotatedVector(vec3, getFacing());
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public static List<BlockPos> getRotatedPositions(List<BlockPos> positions, EnumFacing facing)
    {
        ArrayList<BlockPos> returnList = new ArrayList<>(positions.size());

        for (BlockPos position : positions) {
            switch (facing) {
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

    public static AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth, EnumFacing direction, double[] centerCoords)
    {
        AxisAlignedBB box = null;

        switch (direction) {
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
        switch (facing) {
            case SOUTH:
                return new Vector3f(vector3f.getX(), vector3f.getY(), vector3f.getZ());
            case WEST:
                return new Vector3f(-vector3f.getZ(), vector3f.getY(), vector3f.getX());
            case NORTH:
                return new Vector3f(-vector3f.getX(), vector3f.getY(), -vector3f.getZ());
            case EAST:
                return new Vector3f(vector3f.getZ(), vector3f.getY(), -vector3f.getX());
        }

        return null;
    }

    public static Vec3d getRotatedVector(Vec3d vec3, EnumFacing facing)
    {
        switch (facing) {
            case SOUTH:
                return new Vec3d(vec3.x, vec3.y, vec3.z);
            case WEST:
                return new Vec3d(-vec3.z, vec3.y, vec3.x);
            case NORTH:
                return new Vec3d(-vec3.x, vec3.y, -vec3.z);
            case EAST:
                return new Vec3d(vec3.z, vec3.y, -vec3.x);
        }

        return null;
    }

    public static AxisAlignedBB getBBWithLengths(double x, double y, double z, double width, double height, double depth)
    {
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
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

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }

        return positions;
    }
}
