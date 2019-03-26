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

import ivorius.ivtoolkit.tools.IvNBTHelper;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 11.02.14.
 */
public class IvBlockMapper
{
    private List<String> mapping;

    public IvBlockMapper()
    {
        mapping = new ArrayList<>();
    }

    public IvBlockMapper(NBTTagCompound compound, String key, MCRegistry registry)
    {
        this(compound.getList(key, Constants.NBT.TAG_STRING), registry);
    }

    public IvBlockMapper(NBTTagList list, MCRegistry registry)
    {
        mapping = new ArrayList<>(list.size());

        for (int i = 0; i < list.size(); i++)
            mapping.add(list.getString(i));
    }

    public String getBlock(int mapping)
    {
        return this.mapping.get(mapping);
    }

    public int getMapSize()
    {
        return mapping.size();
    }

    public String[] createBlocksFromNBT(NBTTagCompound compound)
    {
        String[] blocks;

        if (compound.hasKey("blocksCompressed"))
        {
            NBTTagCompound compressed = compound.getCompound("blocksCompressed");
            int[] vals = IvNBTHelper.readCompressed("data", compressed);

            blocks = new String[vals.length];

            for (int i = 0; i < vals.length; i++)
                blocks[i] = getBlock(vals[i]);
        }
        else if (compound.hasKey("blockBytes"))
        {
            byte[] byteArray = compound.getByteArray("blockBytes");
            blocks = new String[byteArray.length];

            for (int i = 0; i < byteArray.length; i++)
                blocks[i] = getBlock(byteArray[i]);
        }
        else if (compound.hasKey("blockInts"))
        {
            int[] intArray = compound.getIntArray("blockInts");
            blocks = new String[intArray.length];

            for (int i = 0; i < intArray.length; i++)
                blocks[i] = getBlock(intArray[i]);
        }
        else
        {
            throw new RuntimeException("Unrecognized block collection type " + compound);
        }

        return blocks;
    }
}
