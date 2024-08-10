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
import ivorius.ivtoolkit.util.IvStreams;
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
        return chunkPos.x == (pos.x >> 4) && chunkPos.z == (pos.z >> 4);
    }

    public static IntStream repeatsInChunk(int chunkPos, int shift, int repeatLength) {
        if (repeatLength == 0) {
            return shift >> 4 == chunkPos
                ? IntStream.of(shift)
                : IntStream.empty();
        }

        int lowest = shift + ((chunkPos << 4) - shift) / repeatLength * repeatLength;
        return IntStream.range(0, repeatLength + 1).map(x -> lowest + x * repeatLength);
    }

    public static Stream<BlockSurfacePos> repeatIntersections(ChunkPos chunkPos, BlockSurfacePos pos, int repeatX, int repeatZ) {
        IntStream xStream = repeatsInChunk(chunkPos.x, pos.x, repeatX);
        IntStream zStream = repeatsInChunk(chunkPos.z, pos.z, repeatZ);

        return IvStreams.flatMapToObj(xStream, x ->
            zStream.mapToObj(z -> new BlockSurfacePos(x, z))
        );
    }
}
