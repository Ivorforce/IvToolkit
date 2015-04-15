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

package ivorius.ivtoolkit.maze.classic;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

/**
 * Created by lukas on 13.04.15.
 */
public class MazeCoordinates
{
    private static TIntObjectMap<MazeRoom[]> cachedNeighborRoomsBlueprints = new TIntObjectHashMap<>();
    private static TIntObjectMap<MazePath[]> cachedNeighborPathBlueprints = new TIntObjectHashMap<>();

    public static MazeRoom rotatedRoom(MazeRoom room, AxisAlignedTransform2D transform, int[] size)
    {
        int[] roomPosition = room.getCoordinates();
        BlockCoord transformedRoom = transform.apply(new BlockCoord(roomPosition[0], roomPosition[1], roomPosition[2]), size);
        return new MazeRoom(transformedRoom.x, transformedRoom.y, transformedRoom.z);
    }

    public static MazePath rotatedPath(MazePath path, AxisAlignedTransform2D transform, int[] size)
    {
        int[] sourceCoords = path.getSourceRoom().getCoordinates();
        int[] destCoords = path.getDestinationRoom().getCoordinates();
        BlockCoord transformedSource = transform.apply(new BlockCoord(sourceCoords[0], sourceCoords[1], sourceCoords[2]), size);
        BlockCoord transformedDest = transform.apply(new BlockCoord(destCoords[0], destCoords[1], destCoords[2]), size);

        return MazePath.fromConnection(new MazeRoom(transformedSource.x, transformedSource.y, transformedSource.z), new MazeRoom(transformedDest.x, transformedDest.y, transformedDest.z));
    }

    public static boolean[] coordPathFlags(MazeCoordinate coordinate)
    {
        int[] mazePosition = coordinate.getMazeCoordinates();
        boolean[] flags = new boolean[mazePosition.length];

        for (int i = 0; i < flags.length; i++)
        {
            flags[i] = mazePosition[i] % 2 == 0;
        }

        return flags;
    }

    private static MazeRoom[] constructNeighborRooms(int dimensions)
    {
        MazePath[] neighborPaths = getNeighborPaths(dimensions);
        MazeRoom[] neighbors = new MazeRoom[neighborPaths.length];

        for (int i = 0; i < neighborPaths.length; i++)
            neighbors[i] = neighborPaths[i].getDestinationRoom();

        return neighbors;
    }

    public static MazeRoom[] getNeighborRooms(int dimensions)
    {
        if (!cachedNeighborRoomsBlueprints.containsKey(dimensions))
        {
            MazeRoom[] neighbors = constructNeighborRooms(dimensions);
            cachedNeighborRoomsBlueprints.put(dimensions, neighbors);
            return neighbors.clone();
        }

        return cachedNeighborRoomsBlueprints.get(dimensions).clone();
    }

    public static MazePath[] getNeighborPaths(int dimensions)
    {
        if (!cachedNeighborPathBlueprints.containsKey(dimensions))
        {
            MazePath[] neighbors = new MazePath[dimensions * 2];
            int[] coordinates = new int[dimensions];

            for (int i = 0; i < dimensions; i++)
            {
                neighbors[i * 2] = MazePath.fromRoom(i, new MazeRoom(coordinates), true);
                neighbors[i * 2 + 1] = MazePath.fromRoom(i, new MazeRoom(coordinates), false);
            }

            cachedNeighborPathBlueprints.put(dimensions, neighbors);
            return neighbors;
        }

        return cachedNeighborPathBlueprints.get(dimensions);
    }

    @Deprecated
    public static MazePath[] getNeighborPaths(int dimensions, MazeRoom mazeRoom)
    {
        return getNeighborPaths(mazeRoom);
    }

    public static MazePath[] getNeighborPaths(MazeRoom mazeRoom)
    {
        MazePath[] blueprints = getNeighborPaths(mazeRoom.getDimensions());
        MazePath[] neighbors = new MazePath[blueprints.length];

        for (int i = 0; i < blueprints.length; i++)
            neighbors[i] = blueprints[i].add(mazeRoom);

        return neighbors;
    }

    public static MazeRoom coordToRoom(MazeCoordinate coordinate)
    {
        if (Maze.isCoordValidRoom(coordinate))
        {
            int[] roomCoord = coordinate.getMazeCoordinates();

            for (int dim = 0; dim < roomCoord.length; dim++)
                roomCoord[dim] = (roomCoord[dim] - 1) / 2;

            return new MazeRoom(roomCoord);
        }

        return null;
    }

    public static MazePath coordToPath(MazeCoordinate coordinate)
    {
        int pathDim = Maze.getPathDimensionIfPath(coordinate);
        return pathDim >= 0 ? coordToPath(coordinate, pathDim) : null;
    }

    public static MazePath coordToPath(MazeCoordinate coordinate, int pathDim)
    {
        int[] roomCoord = coordinate.getMazeCoordinates();
        boolean goesUp = true;

        for (int dim = 0; dim < roomCoord.length; dim++)
        {
            if (roomCoord[dim] == 0)
                goesUp = false;
            else
                roomCoord[dim] = (roomCoord[dim] - 1) / 2;
        }

        return MazePath.fromRoom(pathDim, new MazeRoom(roomCoord), goesUp);
    }
}
