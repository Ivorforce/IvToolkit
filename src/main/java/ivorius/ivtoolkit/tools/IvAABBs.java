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

import net.minecraft.util.AxisAlignedBB;

/**
 * Created by lukas on 14.10.14.
 */
public class IvAABBs
{
    public static AxisAlignedBB intersection(AxisAlignedBB bb, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(Math.max(bb.minX, x), Math.max(bb.minY, y), Math.max(bb.minZ, z),
                Math.min(bb.maxX, x + 1), Math.min(bb.maxY, y + 1), Math.min(bb.maxZ, z + 1));
    }

    public static AxisAlignedBB boundsIntersection(AxisAlignedBB bb, int x, int y, int z)
    {
        return AxisAlignedBB.getBoundingBox(Math.max(bb.minX - x, 0), Math.max(bb.minY - y, 0), Math.max(bb.minZ - z, 0),
                Math.min(bb.maxX - x, 1), Math.min(bb.maxY - y, 1), Math.min(bb.maxZ - z, 1));
    }
}
