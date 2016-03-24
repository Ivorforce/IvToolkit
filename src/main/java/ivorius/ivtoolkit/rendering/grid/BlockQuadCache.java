/*
 * Copyright 2015 Lukas Tenbrink
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

package ivorius.ivtoolkit.rendering.grid;

import com.google.common.base.Function;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.blocks.IvBlockCollection;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Created by lukas on 22.12.14.
 */
public class BlockQuadCache
{
    public static GridQuadCache<?> createQuadCache(final IvBlockCollection blockCollection, float[] scale)
    {
        final Object handle = new Object();

        final int[] size = {blockCollection.width, blockCollection.height, blockCollection.length};

        return GridQuadCache.createQuadCache(size, scale, input -> {
            Block block = blockCollection.getBlock(input.getLeft());
            return block.isOpaqueCube() && blockCollection.shouldRenderSide(input.getLeft(), input.getRight())
                    ? handle
                    : null;
        });
    }
}
