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

import ivorius.ivtoolkit.IvToolkitCoreContainer;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

/**
 * Created by lukas on 21.06.16.
 */
public class NBTStateInjector
{
    public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

    public static void recursivelyInject(NBTBase nbt)
    {
        NBTWalker.walkCompounds(nbt, cmp -> {
            inject(cmp);
            return true;
        });
    }

    public static void inject(NBTTagCompound compound)
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

    public static void addBlockTag(int blockID, NBTTagList tagList, String tagDest)
    {
        Block block = Block.getBlockById(blockID);
        if (block != null)
        {
            String stringID = Block.REGISTRY.getNameForObject(block).toString();

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

    public static void recursivelyApply(NBTBase nbt, MCRegistry registry, boolean remove)
    {
        NBTWalker.walkCompounds(nbt, cmp -> {
            apply(cmp, registry);
            if (remove) cmp.removeTag(ID_FIX_TAG_KEY);
            return true;
        });
    }

    public static void apply(NBTTagCompound compound, MCRegistry registry)
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
}
