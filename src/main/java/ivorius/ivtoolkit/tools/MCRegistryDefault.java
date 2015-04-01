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

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by lukas on 30.06.14.
 */
public class MCRegistryDefault implements MCRegistry
{
    public static final MCRegistryDefault INSTANCE = new MCRegistryDefault();

    @Override
    public Item itemFromID(String itemID)
    {
        return (Item) Item.itemRegistry.getObject(itemID);
    }

    @Override
    public String idFromItem(Item item)
    {
        return Item.itemRegistry.getNameForObject(item);
    }

    @Override
    public void modifyItemStackCompound(NBTTagCompound compound, String itemID)
    {

    }

    @Override
    public Block blockFromID(String blockID)
    {
        return Block.getBlockFromName(blockID);
    }

    @Override
    public String idFromBlock(Block block)
    {
        return Block.blockRegistry.getNameForObject(block);
    }

    @Override
    public TileEntity loadTileEntity(NBTTagCompound compound)
    {
        return TileEntity.createAndLoadEntity(compound);
    }
}
