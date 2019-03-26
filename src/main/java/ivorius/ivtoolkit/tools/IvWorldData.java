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

package ivorius.ivtoolkit.tools;

import com.mojang.datafixers.DataFixer;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.ivtoolkit.transform.Mover;
import ivorius.ivtoolkit.world.MockWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukas on 24.05.14.
 */
public class IvWorldData
{
    public IvBlockCollection blockCollection;
    public List<NBTTagCompound> tileEntities;
    public List<NBTTagCompound> entities;

    public IvWorldData(IvBlockCollection blockCollection, List<NBTTagCompound> tileEntities, List<NBTTagCompound> entities)
    {
        this.blockCollection = blockCollection;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public IvWorldData(NBTTagCompound compound, MCRegistry registry)
    {
        compound = compound.copy(); // Copy since ID fix tags are being removed when being applied
        final DataFixer fixer = DataFixesManager.getDataFixer();

        blockCollection = new IvBlockCollection(compound.getCompound("blockCollection"), registry);

        tileEntities = new ArrayList<>();
        tileEntities.addAll(NBTTagLists.compoundsFrom(compound, "tileEntities"));
        tileEntities.forEach(teCompound -> NBTStateInjector.recursivelyApply(teCompound, registry, false));
        tileEntities.replaceAll(teCompound -> NBTUtil.update(fixer, TypeReferences.BLOCK_ENTITY, teCompound, 0));

        entities = new ArrayList<>();
        entities.addAll(NBTTagLists.compoundsFrom(compound, "entities"));
        entities.forEach(entityCompound -> NBTStateInjector.recursivelyApply(entityCompound, registry, false));
        tileEntities.replaceAll(teCompound -> NBTUtil.update(fixer, TypeReferences.ENTITY, teCompound, 0));
    }

    public static IvWorldData capture(World world, BlockArea blockArea, boolean captureEntities)
    {
        return capture(MockWorld.of(world), blockArea, captureEntities);
    }

    public static IvWorldData capture(MockWorld world, BlockArea blockArea, boolean captureEntities)
    {
        BlockPos referenceCoord = blockArea.getLowerCorner();
        BlockPos invertedReference = BlockPositions.invert(referenceCoord);

        int[] size = blockArea.areaSize();
        IvBlockCollection blockCollection = new IvBlockCollection(size[0], size[1], size[2]);

        List<NBTTagCompound> tileEntities = new ArrayList<>();
        for (BlockPos worldCoord : blockArea)
        {
            BlockPos dataCoord = worldCoord.subtract(blockArea.getLowerCorner());

            blockCollection.setBlockState(dataCoord, world.getBlockState(worldCoord));

            TileEntity tileEntity = world.getTileEntity(worldCoord);
            if (tileEntity != null)
            {
                Mover.moveTileEntity(tileEntity, invertedReference);

                NBTTagCompound teCompound = NBTCompoundObjectsMC.write(tileEntity, true);
                NBTStateInjector.recursivelyInject(teCompound);
                tileEntities.add(teCompound);

                Mover.moveTileEntity(tileEntity, referenceCoord);
            }
        }

        List<NBTTagCompound> entities = captureEntities
                ? world.getEntities(blockArea.asAxisAlignedBB(), saveableEntityPredicate()).stream()
                .map(entity ->
                {
                    Mover.moveEntity(entity, invertedReference);

                    NBTTagCompound entityCompound = NBTCompoundObjectsMC.write(entity, true);
                    NBTStateInjector.recursivelyInject(entityCompound);

                    Mover.moveEntity(entity, referenceCoord);

                    return entityCompound;
                }).collect(Collectors.toList())
                : Collections.emptyList();

        return new IvWorldData(blockCollection, tileEntities, entities);
    }

    public static Predicate<Entity> saveableEntityPredicate()
    {
        return entity -> !(entity instanceof EntityPlayer);
    }

    public NBTTagCompound createTagCompound(MCRegistry registry)
    {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setTag("blockCollection", blockCollection.createTagCompound(registry));
        compound.setTag("tileEntities", NBTTagLists.write(tileEntities));
        compound.setTag("entities", NBTTagLists.write(entities));

        return compound;
    }
}