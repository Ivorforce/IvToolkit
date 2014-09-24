/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.tools;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class IvInventoryHelper
{
    public static boolean consumeInventoryItem(InventoryPlayer inventory, ItemStack itemStack)
    {
        int var2 = getInventorySlotContainItem(inventory, itemStack);

        if (var2 < 0)
        {
            return false;
        }
        else
        {
            if (--inventory.mainInventory[var2].stackSize <= 0)
            {
                inventory.mainInventory[var2] = null;
            }

            return true;
        }
    }

    public static int getInventorySlotContainItem(InventoryPlayer inventory, ItemStack itemStack)
    {
        for (int var2 = 0; var2 < inventory.mainInventory.length; ++var2)
        {
            if (inventory.mainInventory[var2] != null && inventory.mainInventory[var2].isItemEqual(itemStack))
            {
                return var2;
            }
        }

        return -1;
    }

    public static int getInventorySlotContainItem(InventoryPlayer inventory, Item item)
    {
        for (int var2 = 0; var2 < inventory.mainInventory.length; ++var2)
        {
            if (inventory.mainInventory[var2] != null && inventory.mainInventory[var2].getItem() == item)
            {
                return var2;
            }
        }

        return -1;
    }
}
