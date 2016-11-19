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

package ivorius.ivtoolkit.world.chunk.gen;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.structure.StructureBoundingBox;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukas on 01.04.15.
 */
public class StructureBoundingBoxes
{
    public static Set<ChunkPos> rasterize(StructureBoundingBox boundingBox)
    {
        if (boundingBox != null)
        {
            int minX = boundingBox.minX >> 4;
            int maxX = boundingBox.maxX >> 4;

            int minZ = boundingBox.minZ >> 4;
            int maxZ = boundingBox.maxZ >> 4;

            Set<ChunkPos> pairs = new HashSet<>((maxX - minX + 1) * (maxZ - minZ + 1));
            for (int x = minX; x <= maxX; x++)
                for (int z = minZ; z <= maxZ; z++)
                    pairs.add(new ChunkPos(x, z));

            return pairs;
        }

        return Collections.emptySet();
    }

    public static boolean fitsY(StructureBoundingBox boundingBox, int minY, int maxY)
    {
        return boundingBox.minY >= minY && boundingBox.maxY < maxY;
    }

    @Nonnull
    public static StructureBoundingBox wholeHeightBoundingBox(WorldServer world, StructureBoundingBox generationBB)
    {
        StructureBoundingBox toFloorBB = new StructureBoundingBox(generationBB);
        toFloorBB.minY = 1;
        toFloorBB.maxY = world.getHeight();
        return toFloorBB;
    }

    @Nonnull
    public static BlockPos min(StructureBoundingBox boundingBox)
    {
        return new BlockPos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
    }

    @Nonnull
    public static BlockPos max(StructureBoundingBox boundingBox)
    {
        return new BlockPos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }

    public static int[] size(StructureBoundingBox boundingBox)
    {
        return new int[]{boundingBox.getXSize(), boundingBox.getYSize(), boundingBox.getZSize()};
    }
}
