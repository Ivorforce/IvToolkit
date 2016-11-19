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

package ivorius.ivtoolkit.world.chunk;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.tools.IvStreams;
import net.minecraft.util.math.ChunkPos;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lukas on 10.08.16.
 */
public class Chunks
{
    public static boolean contains(ChunkPos chunkPos, BlockSurfacePos pos)
    {
        return chunkPos.chunkXPos == (pos.x >> 4) && chunkPos.chunkZPos == (pos.z >> 4);
    }

    public static Stream<BlockSurfacePos> repeatIntersections(ChunkPos chunkPos, BlockSurfacePos pos, int repeatX, int repeatZ)
    {
        int lowestX = pos.x + (((chunkPos.chunkXPos << 4) - pos.x) / repeatX) * repeatX;
        int lowestZ = pos.z + (((chunkPos.chunkZPos << 4) - pos.z) / repeatZ) * repeatZ;

        int repeatsX = (15 - (lowestX - (chunkPos.chunkXPos << 4))) / repeatX;
        int repeatsZ = (15 - (lowestZ - (chunkPos.chunkZPos << 4))) / repeatZ;

        return IvStreams.flatMapToObj(IntStream.range(0, repeatsX + 1), iX -> IntStream.range(0, repeatsZ + 1).mapToObj(iZ -> new BlockSurfacePos(lowestX + iX * repeatX, lowestZ + iZ * repeatZ)));
    }
}
