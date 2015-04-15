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

package ivorius.ivtoolkit.maze.components;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

/**
 * Created by lukas on 23.06.14.
 */
public class MazeRooms
{
    public static MazeRoom rotated(MazeRoom room, AxisAlignedTransform2D transform, int[] size)
    {
        int[] roomPosition = room.getCoordinates();
        BlockCoord transformedRoom = transform.apply(new BlockCoord(roomPosition[0], roomPosition[1], roomPosition[2]), size);
        return new MazeRoom(transformedRoom.x, transformedRoom.y, transformedRoom.z);
    }
}
