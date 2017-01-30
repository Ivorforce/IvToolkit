/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.transform;

import com.google.common.collect.ImmutableSet;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.math.MinecraftTransforms;
import ivorius.ivtoolkit.tools.EntityCreatureAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by lukas on 09.05.16.
 */
public class PosTransformer
{
    public static void transformTileEntityPos(TileEntity tileEntity, AxisAlignedTransform2D transform, int[] size)
    {
        if (tileEntity instanceof AreaTransformable)
            ((AreaTransformable) tileEntity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
        {
            Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
            tileEntity.func_189668_a(mct.getRight());
            tileEntity.func_189667_a(mct.getLeft());
            Mover.setTileEntityPos(tileEntity, transform.apply(tileEntity.getPos(), size));
        }
    }

    public static void transformEntityPos(Entity entity, AxisAlignedTransform2D transform, int[] size)
    {
        if (entity instanceof AreaTransformable)
            ((AreaTransformable) entity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
        {
            double[] newEntityPos = transform.applyOn(new double[]{entity.posX, entity.posY, entity.posZ}, size);

            Pair<Rotation, Mirror> mct = MinecraftTransforms.to(transform);
            float yaw = entity.getMirroredYaw(mct.getRight());
            yaw = yaw + (entity.rotationYaw - entity.getRotatedYaw(mct.getLeft()));

            entity.setLocationAndAngles(newEntityPos[0], newEntityPos[1], newEntityPos[2], yaw, entity.rotationPitch);

            if (entity instanceof EntityCreature)
            {
                EntityCreature entityCreature = (EntityCreature) entity;
                EntityCreatureAccessor.setHomePosition(entityCreature, transform.apply(entityCreature.getHomePosition(), size));
            }
        }
    }

    public static IBlockState transformBlockState(IBlockState state, AxisAlignedTransform2D transform)
    {
        return MinecraftTransforms.map(transform, (rotation, mirror) -> state.withMirror(mirror).withRotation(rotation));
    }

    @Deprecated
    public static void transformBlock(World world, BlockPos coord, AxisAlignedTransform2D transform)
    {
    }

    @Deprecated
    public static void transformBlock(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block)
    {
        transformBlockDefault(transform, world, state, coord, block);
    }

    @Deprecated
    public static void transformBlockDefault(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block)
    {
        IBlockState newState = state;

        ImmutableSet<Map.Entry<IProperty<?>, Comparable<?>>> propertySet = state.getProperties().entrySet();
        for (Map.Entry<IProperty<?>, Comparable<?>> entry : propertySet)
        {
            IProperty property = entry.getKey();
            if (property.getValueClass() == EnumFacing.class && property.getAllowedValues().containsAll(Arrays.asList(EnumFacing.HORIZONTALS)))
            {
                EnumFacing value = (EnumFacing) entry.getValue();
                newState = newState.withProperty(property, transform.apply(value));
            }
        }

        if (newState != state)
            world.setBlockState(coord, newState);
    }
}
