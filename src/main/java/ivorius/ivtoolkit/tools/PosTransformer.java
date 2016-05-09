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

package ivorius.ivtoolkit.tools;

import com.google.common.collect.ImmutableSet;
import ivorius.ivtoolkit.blocks.BlockTransformable;
import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

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
            Mover.setTileEntityPos(tileEntity, transform.apply(tileEntity.getPos(), size));
    }

    public static void transformEntityPos(Entity entity, AxisAlignedTransform2D transform, int[] size)
    {
        if (entity instanceof AreaTransformable)
            ((AreaTransformable) entity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
        {
            double[] newEntityPos = transform.apply(new double[]{entity.posX, entity.posY, entity.posZ}, size);
            entity.setPosition(newEntityPos[0], newEntityPos[1], newEntityPos[2]);

            if (entity instanceof EntityHanging)
            {
                EntityHanging entityHanging = (EntityHanging) entity;
                BlockPos hangingCoord = entityHanging.getHangingPosition();
                BlockPos newHangingCoord = transform.apply(hangingCoord, size);
                entityHanging.setPosition(newHangingCoord.getX(), newHangingCoord.getY(), newHangingCoord.getZ());
                EntityHangingAccessor.setHangingDirection(entityHanging, Directions.rotate(entityHanging.facingDirection, transform));
            }

            if (entity instanceof EntityCreature)
            {
                EntityCreature entityCreature = (EntityCreature) entity;
                EntityCreatureAccessor.setHomePosition(entityCreature, transform.apply(entityCreature.getHomePosition(), size));
            }
        }
    }

    public static void transformBlock(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block)
    {
        if (block instanceof BlockTransformable)
            ((BlockTransformable) block).transform(world, coord, state, transform.getRotation(), transform.isMirrorX());
        else
            transformBlockDefault(transform, world, state, coord, block);
    }

    public static void transformBlockDefault(AxisAlignedTransform2D transform, World world, IBlockState state, BlockPos coord, Block block)
    {
        IBlockState newState = state;

        ImmutableSet<Map.Entry<IProperty, Comparable>> propertySet = state.getProperties().entrySet();
        for (Map.Entry<IProperty, Comparable> entry : propertySet)
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
