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


import ivorius.ivtoolkit.math.IvMathHelper;
import ivorius.ivtoolkit.math.IvVecMathHelper;
import ivorius.ivtoolkit.raytracing.IvRaytraceableAxisAlignedBox;
import ivorius.ivtoolkit.tools.EnumFacingHelper;
import ivorius.ivtoolkit.tools.IvNBTHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.Constants;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.List;

public class IvTileEntityMultiBlock extends TileEntity
{
    public BlockPos parentCoords;
    public BlockPos[] childCoords;
    public EnumFacing facing;

    public double[] centerCoords = new double[]{0.5, 0.5, 0.5};
    public double[] centerCoordsSize = new double[]{0.5, 0.5, 0.5};

    public boolean multiblockInvalid;

    public IvTileEntityMultiBlock()
    {

    }

    public BlockPos getActiveParentCoords()
    {
        if (parentCoords == null)
            return getPos();

        return getPos().add(parentCoords.getX(), parentCoords.getY(), parentCoords.getZ());
    }

    public BlockPos[] getActiveChildCoords()
    {
        if (childCoords == null)
            return new BlockPos[0];

        BlockPos[] returnVal = new BlockPos[childCoords.length];
        for (int i = 0; i < returnVal.length; i++)
            returnVal[i] = getPos().add(childCoords[i]);

        return returnVal;
    }

    public double[] getActiveCenterCoords()
    {
        if (centerCoords == null)
            return new double[]{getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getY() + 0.5};

        return new double[]{getPos().getX() + centerCoords[0], getPos().getY() + centerCoords[1], getPos().getZ() + centerCoords[2]};
    }

    public void becomeChild(TileEntity parent)
    {
        if (parent != null)
            this.parentCoords = BlockPositions.sub(parent.getPos(), getPos());
        else
            this.parentCoords = null;
    }

    public void becomeParent(List<BlockPos> childCoords)
    {
        if (childCoords != null)
        {
            this.childCoords = new BlockPos[childCoords.size()];

            for (int i = 0; i < childCoords.size(); i++)
                this.childCoords[i] = BlockPositions.sub(childCoords.get(i), getPos());
        }
        else
            this.childCoords = new BlockPos[0];
    }

    public boolean isParent()
    {
        return parentCoords == null;
    }

    public IvTileEntityMultiBlock getTotalParent()
    {
        return isParent() ? this : getParent();
    }

    public IvTileEntityMultiBlock getParent()
    {
        if (parentCoords != null)
        {
            TileEntity parentTileEntity = world.getTileEntity(getActiveParentCoords());

            if (parentTileEntity != null && this.getClass().equals(parentTileEntity.getClass()))
                return (IvTileEntityMultiBlock) parentTileEntity;
        }

        return null;
    }

    public float getDistanceToCenter()
    {
        return (float) IvVecMathHelper.distance(new double[]{getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5}, getActiveCenterCoords());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);

        if (tagCompound.hasKey("direction", Constants.NBT.TAG_INT)) // Legacy
        {
            switch (tagCompound.getInteger("direction"))
            {
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

        centerCoords = IvNBTHelper.readDoubleArray("multiBlockCenter", tagCompound);
        if (centerCoords == null) // Legacy
        {
            centerCoords = new double[3];

            if (tagCompound.hasKey("centerCoords[0]"))
                centerCoords[0] = tagCompound.getDouble("centerCoords[0]") - getPos().getX();
            else
                centerCoords[0] = 0.5;

            if (tagCompound.hasKey("centerCoords[1]"))
                centerCoords[1] = tagCompound.getDouble("centerCoords[1]") - getPos().getY();
            else
                centerCoords[1] = 0.5;

            if (tagCompound.hasKey("centerCoords[2]"))
                centerCoords[2] = tagCompound.getDouble("centerCoords[2]") - getPos().getZ();
            else
                centerCoords[2] = 0.5;
        }

        centerCoordsSize = IvNBTHelper.readDoubleArray("multiBlockSize", tagCompound);
        if (centerCoordsSize == null) // Legacy
        {
            centerCoordsSize = new double[3];
            if (tagCompound.hasKey("centerCoordsSize[0]"))
                centerCoordsSize[0] = tagCompound.getFloat("centerCoordsSize[0]");
            else
                centerCoordsSize[0] = 0.5;

            if (tagCompound.hasKey("centerCoordsSize[1]"))
                centerCoordsSize[1] = tagCompound.getFloat("centerCoordsSize[1]");
            else
                centerCoordsSize[1] = 0.5;

            if (tagCompound.hasKey("centerCoordsSize[2]"))
                centerCoordsSize[2] = tagCompound.getFloat("centerCoordsSize[2]");
            else
                centerCoordsSize[2] = 0.5;
        }

        if (tagCompound.hasKey("parentCoords"))
            this.parentCoords = BlockPositions.fromIntArray(IvNBTHelper.readIntArrayFixedSize("parentCoords", 3, tagCompound));
        else
            this.parentCoords = null;

        if (tagCompound.hasKey("childCoords"))
        {
            int[] childCoordsCut = tagCompound.getIntArray("childCoords");
            childCoords = new BlockPos[childCoordsCut.length / 3];
            for (int i = 0; i < childCoords.length; i++)
                childCoords[i] = new BlockPos(childCoordsCut[i * 3], childCoordsCut[i * 3 + 1], childCoordsCut[i * 3 + 2]);
        }
        else if (parentCoords == null)
            childCoords = new BlockPos[0];

        multiblockInvalid = tagCompound.getBoolean("multiblockInvalid");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        tagCompound.setString("facing", facing.getName());

        IvNBTHelper.writeDoubleArray("multiBlockCenter", centerCoords, tagCompound);
        IvNBTHelper.writeDoubleArray("multiBlockSize", centerCoordsSize, tagCompound);

        if (this.parentCoords != null)
            tagCompound.setIntArray("parentCoords", BlockPositions.toIntArray(parentCoords));

        if (childCoords != null)
        {
            int[] childCoordsCut = new int[childCoords.length * 3];
            for (int i = 0; i < childCoords.length; i++)
            {
                childCoordsCut[i * 3] = childCoords[i].getX();
                childCoordsCut[i * 3 + 1] = childCoords[i].getY();
                childCoordsCut[i * 3 + 2] = childCoords[i].getZ();
            }

            tagCompound.setIntArray("childCoords", childCoordsCut);
        }
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
        readFromNBT(pkt.getNbtCompound());
    }

    public IvRaytraceableAxisAlignedBox getInterpolatedRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth, double x1, double y1, double z1, double width1, double height1, double depth1, float fraction)
    {
        double xI = IvMathHelper.mix(x, x1, fraction);
        double yI = IvMathHelper.mix(y, y1, fraction);
        double zI = IvMathHelper.mix(z, z1, fraction);

        double wI = IvMathHelper.mix(width, width1, fraction);
        double hI = IvMathHelper.mix(height, height1, fraction);
        double dI = IvMathHelper.mix(depth, depth1, fraction);

        return getRotatedBox(userInfo, xI, yI, zI, wI, hI, dI);
    }

    public IvRaytraceableAxisAlignedBox getRotatedBox(Object userInfo, double x, double y, double z, double width, double height, double depth)
    {
        return IvMultiBlockHelper.getRotatedBox(userInfo, x, y, z, width, height, depth, getFacing(), getActiveCenterCoords());
    }

    public AxisAlignedBB getRotatedBB(double x, double y, double z, double width, double height, double depth)
    {
        return IvMultiBlockHelper.getRotatedBB(x, y, z, width, height, depth, getFacing(), getActiveCenterCoords());
    }

    public Vector3f getRotatedVector(Vector3f vector3f)
    {
        return IvMultiBlockHelper.getRotatedVector(vector3f, getFacing());
    }

    public Vec3d getRotatedVector(Vec3d vec3)
    {
        return IvMultiBlockHelper.getRotatedVector(vec3, getFacing());
    }

    public EnumFacing getFacing()
    {
        return facing;
    }

    public AxisAlignedBB getBoxAroundCenter(double width, double height, double length)
    {
        double[] center = getActiveCenterCoords();
        return new AxisAlignedBB(center[0] - width, center[1] - height, center[2] - length, center[0] + width, center[1] + height, center[2] + length);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        if (!isParent())
        {
            return getBoxAroundCenter(0.0, 0.0, 0.0);
        }
        else
        {
            return getBoxAroundCenter(centerCoordsSize[0], centerCoordsSize[1], centerCoordsSize[2]);
        }
    }
}
