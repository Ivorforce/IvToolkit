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

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.Ranges;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukas on 23.06.14.
 */
public class MazeRooms
{
    public static MazeRoom rotated(MazeRoom room, AxisAlignedTransform2D transform, int[] size)
    {
        if (room.getDimensions() < 3)
            throw new IllegalArgumentException();
        
        int[] roomPosition = room.getCoordinates();
        int[] transformedRoom = transform.apply(roomPosition, size);

        roomPosition[0] = transformedRoom[0];
        roomPosition[1] = transformedRoom[1];
        roomPosition[2] = transformedRoom[2];

        return new MazeRoom(roomPosition);
    }

    public static Set<MazeRoomConnection> neighbors(final MazeRoom room, TIntSet dimensions)
    {
        final Set<MazeRoomConnection> set = new HashSet<>(dimensions.size() * 2);
        dimensions.forEach(new TIntProcedure()
        {
            @Override
            public boolean execute(int value)
            {
                set.add(new MazeRoomConnection(room, room.addInDimension(value, 1)));
                set.add(new MazeRoomConnection(room, room.addInDimension(value, -1)));
                return true;
            }
        });
        return set;
    }

    public static Set<MazeRoomConnection> neighbors(MazeRoom room)
    {
        return neighbors(room, new TIntHashSet(Ranges.to(room.getDimensions())));
    }
}
