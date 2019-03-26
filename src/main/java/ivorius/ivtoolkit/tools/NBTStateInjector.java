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

import com.google.common.primitives.Ints;
import ivorius.ivtoolkit.IvToolkit;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by lukas on 21.06.16.
 */
public class NBTStateInjector
{
    public static final String ID_FIX_TAG_KEY = "SG_ID_FIX_TAG";

    public static void recursivelyInject(INBTBase nbt)
    {
        NBTWalker.walkCompounds(nbt, cmp ->
        {
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

        if (list.size() > 0)
        {
            compound.setTag(ID_FIX_TAG_KEY, list);
        }
    }

    private static boolean hasPrimitive(NBTTagCompound compound, String key)
    {
        return compound.hasKey(key) && compound.getTag(key) instanceof NBTPrimitive;
    }

    public static void injectTEBlockFixTags(NBTTagCompound compound, String tileEntityID, NBTTagList list, String... keys)
    {
        if (tileEntityID.equals(compound.getString("id")))
        {
            for (String key : keys)
                if (hasPrimitive(compound, key))
                    addBlockTag(compound.getInt(key), list, key);
        }
    }

    public static void addBlockTag(int blockID, NBTTagList tagList, String tagDest)
    {
        IBlockState state = Block.getStateById(blockID);
        if (state != Blocks.AIR.getDefaultState())
        {
            String stringID = ForgeRegistries.BLOCKS.getKey(state.getBlock()).toString();

            NBTTagCompound idCompound = new NBTTagCompound();
            idCompound.setString("type", "block");
            idCompound.setString("tagDest", tagDest);
            idCompound.setString("blockID", stringID);

            tagList.add(idCompound);
        }
        else
        {
            IvToolkit.logger.warn("Failed to apply block tag for structure with ID '" + blockID + "'");
        }
    }

    public static void recursivelyApply(INBTBase nbt, MCRegistry registry, boolean remove)
    {
        NBTWalker.walkCompounds(nbt, cmp ->
        {
            apply(cmp, registry);
            if (remove) cmp.removeTag(ID_FIX_TAG_KEY);
            return true;
        });
    }

    public static void apply(NBTTagCompound compound, MCRegistry registry)
    {
        if (compound.hasKey(ID_FIX_TAG_KEY))
        {
            NBTTagList list = compound.getList(ID_FIX_TAG_KEY, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++)
                applyIDFixTag(compound, registry, list.getCompound(i));
        }

        if (compound.hasKey("id") && compound.hasKey("Count") && compound.hasKey("Damage")) // Prooobably an item stack
        {
            if (Ints.tryParse(compound.getString("id")) == null) // If this is null, we have a String ID
                registry.modifyItemStackCompound(compound, new ResourceLocation(compound.getString("id")));
        }
    }

    public static void applyIDFixTag(NBTTagCompound compound, MCRegistry registry, NBTTagCompound fixTag)
    {
        String type = fixTag.getString("type");

        switch (type)
        {
            case "item":
            {
                // Items now read Strings \o/
                compound.setString(fixTag.getString("tagDest"), fixTag.getString("itemID"));

                break;
            }
            case "block":
            {
                String dest = fixTag.getString("tagDest");
                ResourceLocation blockID = new ResourceLocation(fixTag.getString("blockID"));

                Block block = registry.blockFromID(blockID);
                if (block != null)
                    compound.setInt(dest, Block.getStateId(block.getDefaultState()));
                else
                    IvToolkit.logger.warn("Failed to fix block tag from structure with ID '" + fixTag.getString("blockID") + "'");
                break;
            }
            default:
                IvToolkit.logger.warn("Unrecognized ID fix tag in structure with type '" + type + "'");
                break;
        }
    }
}
