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

import com.google.common.base.Predicate;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lukas on 24.05.14.
 */
public class IvWorldData
{
    public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

    public IvBlockCollection blockCollection;
    public List<NBTTagCompound> tileEntities;
    public List<NBTTagCompound> entities;

    public IvWorldData(IvBlockCollection blockCollection, List<NBTTagCompound> tileEntities, List<NBTTagCompound> entities)
    {
        this.blockCollection = blockCollection;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public static IvWorldData capture(World world, BlockArea blockArea, boolean captureEntities)
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

                NBTTagCompound teCompound = NBTCompoundObjectsMC.write(tileEntity);
                NBTStateInjector.recursivelyInject(teCompound);
                tileEntities.add(teCompound);

                Mover.moveTileEntity(tileEntity, referenceCoord);
            }
        }

        List<NBTTagCompound> entities = captureEntities
                ? world.getEntitiesInAABBexcluding(null, blockArea.asAxisAlignedBB(), saveableEntityPredicate()).stream()
                .map(entity -> {
                    Mover.moveEntity(entity, invertedReference);

                    NBTTagCompound entityCompound = NBTCompoundObjectsMC.write(entity);
                    NBTStateInjector.recursivelyInject(entityCompound);

                    Mover.moveEntity(entity, referenceCoord);

                    return entityCompound;
                }).collect(Collectors.toList())
                : Collections.emptyList();

        return new IvWorldData(blockCollection, tileEntities, entities);
    }

    public IvWorldData(NBTTagCompound compound, MCRegistry registry)
    {
        compound = (NBTTagCompound) compound.copy(); // Copy since ID fix tags are being removed when being applied

        blockCollection = new IvBlockCollection(compound.getCompoundTag("blockCollection"), registry);

        NBTTagList teList = compound.getTagList("tileEntities", Constants.NBT.TAG_COMPOUND);
        tileEntities = new ArrayList<>(teList.tagCount());
        for (int i = 0; i < teList.tagCount(); i++)
        {
            NBTTagCompound teCompound = teList.getCompoundTagAt(i);
            NBTStateInjector.recursivelyApply(teCompound, registry, false);
            tileEntities.add(teCompound);
        }

        NBTTagList entityList = compound.getTagList("entities", Constants.NBT.TAG_COMPOUND);
        entities = new ArrayList<>(entityList.tagCount());
        for (int i = 0; i < entityList.tagCount(); i++)
        {
            NBTTagCompound entityCompound = entityList.getCompoundTagAt(i);
            NBTStateInjector.recursivelyApply(entityCompound, registry, false);
            entities.add(entityCompound);
        }
    }

    public static Predicate<Entity> saveableEntityPredicate()
    {
        return entity -> !(entity instanceof EntityPlayer);
    }

    public NBTTagCompound createTagCompound(BlockPos referenceCoord)
    {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setTag("blockCollection", blockCollection.createTagCompound());
        compound.setTag("tileEntities", NBTTagLists.write(tileEntities));
        compound.setTag("entities", NBTTagLists.write(tileEntities));

        return compound;
    }
}