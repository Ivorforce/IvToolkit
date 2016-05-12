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

import net.minecraft.util.EnumFacing;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Created by lukas on 26.07.14.
 */
public class IvRotatableBlockRenderHelper
{
    public static void transformFor(IvTileEntityRotatable tileEntity, double renderX, double renderY, double renderZ)
    {
        GlStateManager.translate(renderX + 0.5, renderY + 0.5, renderZ + 0.5);
        GlStateManager.rotate(getAngleFromSouth(tileEntity.getFacing()), 0.0f, 1.0f, 0.0f);
    }

    public static float getAngleFromSouth(EnumFacing facing)
    {
        switch (facing)
        {
            case SOUTH:
                return 180.0f;
            case WEST:
                return 90.0f;
            case NORTH:
                return 0.0f;
            case EAST:
                return 270.0f;
            default:
                throw new IllegalArgumentException();
        }
    }
}
