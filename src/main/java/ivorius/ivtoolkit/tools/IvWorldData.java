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
import ivorius.ivtoolkit.IvToolkitCoreContainer;
import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 24.05.14.
 */
public class IvWorldData
{
    public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

    public IvBlockCollection blockCollection;
    public List<TileEntity> tileEntities;
    public List<Entity> entities;

    public IvWorldData(IvBlockCollection blockCollection, List<TileEntity> tileEntities, List<Entity> entities)
    {
        this.blockCollection = blockCollection;
        this.tileEntities = tileEntities;
        this.entities = entities;
    }

    public IvWorldData(World world, BlockArea blockArea, boolean captureEntities)
    {
        int[] size = blockArea.areaSize();
        blockCollection = new IvBlockCollection(size[0], size[1], size[2]);

        tileEntities = new ArrayList<>();
        for (BlockPos worldCoord : blockArea)
        {
            BlockPos dataCoord = worldCoord.subtract(blockArea.getLowerCorner());

            blockCollection.setBlockState(dataCoord, world.getBlockState(worldCoord));

            TileEntity tileEntity = world.getTileEntity(worldCoord);
            if (tileEntity != null)
                tileEntities.add(tileEntity);
        }

        entities = captureEntities
                ? world.getEntitiesWithinAABB(null, blockArea.asAxisAlignedBB(), saveableEntityPredicate())
                : Collections.emptyList();
    }

    public IvWorldData(NBTTagCompound compound, World world, MCRegistry registry)
    {
        compound = (NBTTagCompound) compound.copy(); // Copy since ID fix tags are being removed when being applied

        blockCollection = new IvBlockCollection(compound.getCompoundTag("blockCollection"), registry);

        NBTTagList teList = compound.getTagList("tileEntities", Constants.NBT.TAG_COMPOUND);
        tileEntities = new ArrayList<>(teList.tagCount());
        for (int i = 0; i < teList.tagCount(); i++)
        {
            NBTTagCompound teCompound = teList.getCompoundTagAt(i);
            recursivelyApplyIDFixTags(teCompound, registry);
            TileEntity tileEntity = registry.loadTileEntity(teCompound);

            if (tileEntity != null)
                tileEntities.add(tileEntity);
        }

        if (world != null)
        {
            NBTTagList entityList = compound.getTagList("entities", Constants.NBT.TAG_COMPOUND);
            entities = new ArrayList<>(entityList.tagCount());
            for (int i = 0; i < entityList.tagCount(); i++)
            {
                NBTTagCompound entityCompound = entityList.getCompoundTagAt(i);
                recursivelyApplyIDFixTags(entityCompound, registry);
                Entity entity = EntityList.createEntityFromNBT(entityCompound, world);

                if (entity != null)
                    entities.add(entity);
            }
        }
    }

    public static Predicate<Entity> saveableEntityPredicate()
    {
        return entity -> !(entity instanceof EntityPlayer);
    }

    public static void recursivelyInjectIDFixTags(NBTTagCompound compound)
    {
        injectIDFixTags(compound);

        for (Object key : compound.getKeySet())
        {
            String keyString = (String) key;
            NBTBase innerCompound = compound.getTag(keyString);

            if (innerCompound instanceof NBTTagCompound)
            {
                recursivelyInjectIDFixTags((NBTTagCompound) innerCompound);
            }
            else if (innerCompound instanceof NBTTagList)
            {
                recursivelyInjectIDFixTags((NBTTagList) innerCompound);
            }
        }
    }

    public static void recursivelyInjectIDFixTags(NBTTagList list)
    {
        int tagType = list.getTagType();
        switch (tagType)
        {
            case Constants.NBT.TAG_COMPOUND:
                for (int i = 0; i < list.tagCount(); i++)
                {
                    recursivelyInjectIDFixTags(list.getCompoundTagAt(i));
                }
                break;
            case Constants.NBT.TAG_LIST:
//                for (int i = 0; i < list.tagCount(); i++)
//                {
//                    recursivelyInjectIDFixTags(list.getCompoundTagAt(i));
//                }
                break;
        }
    }

    private static boolean hasPrimitive(NBTTagCompound compound, String key)
    {
        return compound.hasKey(key) && compound.getTag(key) instanceof NBTBase.NBTPrimitive;
    }

    public static void injectTEBlockFixTags(NBTTagCompound compound, String tileEntityID, NBTTagList list, String... keys)
    {
        if (tileEntityID.equals(compound.getString("id")))
        {
            for (String key : keys)
                if (hasPrimitive(compound, key))
                    addBlockTag(compound.getInteger(key), list, key);
        }
    }

    public static void injectIDFixTags(NBTTagCompound compound)
    {
        NBTTagList list = new NBTTagList();

        injectTEBlockFixTags(compound, "vanishingTileEntity", list, "BlockID");
        injectTEBlockFixTags(compound, "fenceGateTileEntity", list, "camoBlock");
        injectTEBlockFixTags(compound, "mixedBlockTileEntity", list, "block1", "block2");
        injectTEBlockFixTags(compound, "customDoorTileEntity", list, "frame", "topMaterial", "bottomMaterial");

        if (list.tagCount() > 0)
        {
            compound.setTag(ID_FIX_TAG_KEY, list);
        }
    }

    public static void addBlockTag(int blockID, NBTTagList tagList, String tagDest)
    {
        Block block = Block.getBlockById(blockID);
        if (block != null)
        {
            String stringID = Block.blockRegistry.getNameForObject(block).toString();

            NBTTagCompound idCompound = new NBTTagCompound();
            idCompound.setString("type", "block");
            idCompound.setString("tagDest", tagDest);
            idCompound.setString("blockID", stringID);

            tagList.appendTag(idCompound);
        }
        else
        {
            IvToolkitCoreContainer.logger.warn("Failed to apply block tag for structure with ID '" + blockID + "'");
        }
    }

    public static void recursivelyApplyIDFixTags(NBTTagCompound compound, MCRegistry registry)
    {
        applyIDFixTags(compound, registry);
        compound.removeTag(ID_FIX_TAG_KEY);

        for (Object key : compound.getKeySet())
        {
            String keyString = (String) key;
            NBTBase innerCompound = compound.getTag(keyString);

            if (innerCompound instanceof NBTTagCompound)
            {
                recursivelyApplyIDFixTags((NBTTagCompound) innerCompound, registry);
            }
            else if (innerCompound instanceof NBTTagList)
            {
                recursivelyApplyIDFixTags((NBTTagList) innerCompound, registry);
            }
        }
    }

    public static void recursivelyApplyIDFixTags(NBTTagList list, MCRegistry registry)
    {
        int tagType = list.getTagType();
        switch (tagType)
        {
            case Constants.NBT.TAG_COMPOUND:
                for (int i = 0; i < list.tagCount(); i++)
                {
                    recursivelyApplyIDFixTags(list.getCompoundTagAt(i), registry);
                }
                break;
            case Constants.NBT.TAG_LIST:
//                for (int i = 0; i < list.tagCount(); i++)
//                {
//                    recursivelyApplyIDFixTags(list.getCompoundTagAt(i));
//                }
                break;
        }
    }

    public static void applyIDFixTags(NBTTagCompound compound, MCRegistry registry)
    {
        if (compound.hasKey(ID_FIX_TAG_KEY))
        {
            NBTTagList list = compound.getTagList(ID_FIX_TAG_KEY, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++)
                applyIDFixTag(compound, registry, list.getCompoundTagAt(i));
        }
    }

    public static void applyIDFixTag(NBTTagCompound compound, MCRegistry registry, NBTTagCompound fixTag)
    {
        String type = fixTag.getString("type");

        switch (type)
        {
            case "item":
            {
                String dest = fixTag.getString("tagDest");
                String stringID = fixTag.getString("itemID");
                Item item = registry.itemFromID(new ResourceLocation(stringID));
                if (item != null)
                    compound.setInteger(dest, Item.getIdFromItem(item));
                else
                    IvToolkitCoreContainer.logger.warn("Failed to fix item tag from structure with ID '" + stringID + "'");

                registry.modifyItemStackCompound(compound, new ResourceLocation(stringID));
                break;
            }
            case "block":
            {
                String dest = fixTag.getString("tagDest");
                String stringID = fixTag.getString("blockID");
                Block block = registry.blockFromID(new ResourceLocation(stringID));
                if (block != null)
                    compound.setInteger(dest, Block.getIdFromBlock(block));
                else
                    IvToolkitCoreContainer.logger.warn("Failed to fix block tag from structure with ID '" + stringID + "'");
                break;
            }
            default:
                IvToolkitCoreContainer.logger.warn("Unrecognized ID fix tag in structure with type '" + type + "'");
                break;
        }
    }

    public NBTTagCompound createTagCompound(BlockPos referenceCoord)
    {
        BlockPos invertedReference = BlockPositions.invert(referenceCoord);

        NBTTagCompound compound = new NBTTagCompound();

        compound.setTag("blockCollection", blockCollection.createTagCompound());

        NBTTagList teList = new NBTTagList();
        for (TileEntity tileEntity : tileEntities)
        {
            NBTTagCompound teCompound = new NBTTagCompound();

            Mover.moveTileEntityForGeneration(tileEntity, invertedReference);
            tileEntity.writeToNBT(teCompound);
            Mover.moveTileEntityForGeneration(tileEntity, referenceCoord);

            recursivelyInjectIDFixTags(teCompound);
            teList.appendTag(teCompound);
        }
        compound.setTag("tileEntities", teList);

        NBTTagList entityList = new NBTTagList();
        for (Entity entity : entities)
        {
            NBTTagCompound entityCompound = new NBTTagCompound();

            Mover.moveEntityForGeneration(entity, invertedReference);
            entity.writeToNBTOptional(entityCompound);
            Mover.moveEntityForGeneration(entity, referenceCoord);

            recursivelyInjectIDFixTags(entityCompound);
            entityList.appendTag(entityCompound);
        }
        compound.setTag("entities", entityList);

        return compound;

    }
}