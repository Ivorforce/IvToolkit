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

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;

/**
 * Created by lukas on 06.05.16.
 */
public class BlockStates
{
    public static int toMetadata(IBlockState state)
    {
        return state.getBlock().getMetaFromState(state);
    }

    public static IBlockState fromMetadata(Block block, int metadata)
    {
        //noinspection deprecation
        return block.getStateFromMeta(metadata);
    }

    public static IBlockState readBlockState(ByteBuf buf)
    {
        Block block = Block.REGISTRY.getObject(new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
        //noinspection deprecation
        return block.getStateFromMeta(buf.readInt());
    }

    public static void writeBlockState(ByteBuf buf, IBlockState state)
    {
        ByteBufUtils.writeUTF8String(buf, Block.REGISTRY.getNameForObject(state.getBlock()).toString());
        buf.writeInt(BlockStates.toMetadata(state));
    }
}
