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

package ivorius.ivtoolkit.blocks;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Created by lukas on 22.09.16.
 */
public class IvMutableBlockPos
{
    public static BlockPos.MutableBlockPos add(BlockPos.MutableBlockPos pos, BlockPos add)
    {
        return pos.setPos(pos.getX() + add.getX(), pos.getY() + add.getY(), pos.getZ() + add.getZ());
    }

    public static BlockPos.MutableBlockPos offset(BlockPos pos, BlockPos.MutableBlockPos dest, EnumFacing facing)
    {
        return offset(pos, dest, facing, 1);
    }

    public static BlockPos.MutableBlockPos offset(BlockPos pos, BlockPos.MutableBlockPos dest, EnumFacing facing, int amount)
    {
        return dest.setPos(pos.getX() + facing.getFrontOffsetX() * amount, pos.getY() + facing.getFrontOffsetY() * amount, pos.getZ() + facing.getFrontOffsetZ() * amount);
    }
}
