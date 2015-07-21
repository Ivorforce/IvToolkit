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

package ivorius.ivtoolkit.tools;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityHanging;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/**
 * Created by lukas on 21.07.15.
 */
public class Mover
{
    public static void moveTileEntityForGeneration(TileEntity tileEntity, BlockPos coord)
    {
        if (tileEntity instanceof Movable)
            ((Movable) tileEntity).move(coord);
        else
            tileEntity.setPos(tileEntity.getPos().add(coord));
    }

    public static void setTileEntityPosForGeneration(TileEntity tileEntity, BlockPos coord)
    {
        moveTileEntityForGeneration(tileEntity, coord.subtract(tileEntity.getPos()));
    }

    public static void transformTileEntityPosForGeneration(TileEntity tileEntity, AxisAlignedTransform2D transform, int[] size)
    {
        if (tileEntity instanceof Transformable)
            ((Transformable) tileEntity).transform(transform.getRotation(), transform.isMirrorX(), size);
        else
            setTileEntityPosForGeneration(tileEntity, transform.apply(tileEntity.getPos(), size));
    }

    public static void moveEntityForGeneration(Entity entity, BlockPos coord)
    {
        if (entity instanceof Movable)
            ((Movable) entity).move(coord);
        else
        {
            entity.setPosition(entity.posX + coord.getX(), entity.posY + coord.getY(), entity.posZ + coord.getZ());

            if (entity instanceof EntityHanging)
            {
                EntityHanging entityHanging = (EntityHanging) entity;
                BlockPos hangingPosition = entityHanging.getHangingPosition().add(coord);
                entityHanging.setPosition(hangingPosition.getX(), hangingPosition.getY(), hangingPosition.getZ());
            }

            if (entity instanceof EntityCreature)
            {
                EntityCreature entityCreature = (EntityCreature) entity;
                EntityCreatureAccessor.setHomePosition(entityCreature, entityCreature.getHomePosition().add(coord));
            }
        }
    }

    public static void transformEntityPosForGeneration(Entity entity, AxisAlignedTransform2D transform, int[] size)
    {
        if (entity instanceof Transformable)
            ((Transformable) entity).transform(transform.getRotation(), transform.isMirrorX(), size);
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
}
