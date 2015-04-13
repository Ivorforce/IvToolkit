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

package ivorius.ivtoolkit.maze;

import ivorius.ivtoolkit.blocks.BlockCoord;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;
import net.minecraft.util.WeightedRandom;

import java.util.*;

public class MazeGenerator
{
    public static void generateStartPathsForEnclosedMaze(Maze maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform)
    {
        int[] mazeSizeInRooms = new int[maze.dimensions.length];
        for (int i = 0; i < mazeSizeInRooms.length; i++)
            mazeSizeInRooms[i] = (maze.dimensions[i] - 1) / 2;

        for (MazePath path : maze.allPaths())
        {
            if (maze.isPathPointingOutside(path))
                maze.set(Maze.WALL, path);
        }

        for (MazeRoom blockedRoom : blockedRooms)
        {
            blockedRoom = rotatedRoom(blockedRoom, transform, mazeSizeInRooms);

            maze.set(Maze.WALL, blockedRoom);
            for (int dim = 0; dim < maze.dimensions.length; dim++)
            {
                maze.set(Maze.WALL, new MazePath(blockedRoom, dim, true));
                maze.set(Maze.WALL, new MazePath(blockedRoom, dim, false));
            }
        }

        for (MazePath startPoint : startPoints)
            maze.set(Maze.ROOM, rotatedPath(startPoint, transform, mazeSizeInRooms));
    }

    public static MazePath randomEmptyPathInMaze(Random rand, Maze maze, Collection<Integer> applicableDimensions)
    {
        List<MazePath> paths = new ArrayList<>(maze.allPaths());
        for (Iterator<MazePath> iterator = paths.iterator(); iterator.hasNext(); )
        {
            MazePath path = iterator.next();
            if (maze.get(path) != Maze.NULL || !applicableDimensions.contains(path.pathDimension))
                iterator.remove();
        }

        if (paths.size() > 0)
            return paths.get(rand.nextInt(paths.size()));

        return null;
    }

    @Deprecated
    public static MazeRoom randomRoomInMaze(Random rand, Maze maze, int... distanceFromOutside)
    {
        int[] position = new int[maze.dimensions.length];
        for (int i = 0; i < maze.dimensions.length; i++)
        {
            position[i] = rand.nextInt(maze.dimensions[i] / 2 - distanceFromOutside[i]);
        }

        return new MazeRoom(position);
    }

    @Deprecated
    public static MazePath randomPathInMaze(Random rand, Maze maze, int... distanceFromOutside)
    {
        List<WeightedSelector.SimpleItem<Integer>> dimensionWeights = new ArrayList<>();
        for (int dim = 0; dim < maze.dimensions.length; dim++)
        {
            int dimLength = (maze.dimensions[dim] / 2) + 1 - ((distanceFromOutside[dim] + 1) / 2) * 2;
            dimensionWeights.add(new WeightedSelector.SimpleItem<>(Math.max(0, dimLength), dim));
        }
        int usedDimension = WeightedSelector.select(rand, dimensionWeights);

        int[] roomDistanceFromOutside = new int[distanceFromOutside.length];
        for (int i = 0; i < roomDistanceFromOutside.length; i++)
            roomDistanceFromOutside[i] = distanceFromOutside[i] / 2;

        MazeRoom refRoom = randomRoomInMaze(rand, maze, roomDistanceFromOutside);
        int[] pathCoord = refRoom.getMazeCoordinates();
        int availablePaths = ((distanceFromOutside[usedDimension] + 1) / 2) * 2;
        pathCoord[usedDimension] = rand.nextInt(availablePaths) * 2 + (maze.dimensions[usedDimension] / 2 + 1 - availablePaths) / 2;

        return Maze.coordToPath(new MazeCoordinateDirect(pathCoord), usedDimension);
    }

    public static void generatePaths(Random rand, Maze maze, int[] pathWeights, MazeRoom startPoint)
    {
        for (int i = 0; i < maze.dimensions.length; i++)
        {
            if (maze.dimensions[i] < 3)
            {
                return;
            }
        }

        MazeRoom position = startPoint.clone();
        maze.set(Maze.ROOM, position);

        Stack<MazeRoom> positionStack = new Stack<>();

        ArrayList<MazePath> validRoomNeighbors = new ArrayList<>();

        while (true)
        {
            validRoomNeighbors.clear();

            for (MazePath neighbor : Maze.getNeighborPaths(position))
            {
                if (maze.get(neighbor.getDestinationRoom()) == Maze.NULL)
                {
                    for (int n = 0; n < pathWeights[neighbor.pathDimension]; n++)
                    {
                        validRoomNeighbors.add(neighbor);
                    }
                }
            }

            if (validRoomNeighbors.size() == 0)
            {
                if (positionStack.empty())
                {
                    break;
                }

                position = positionStack.pop();

                continue;
            }

            positionStack.push(position.clone());

            MazePath usedPath = validRoomNeighbors.get(rand.nextInt(validRoomNeighbors.size()));
            MazeRoom destRoom = usedPath.getDestinationRoom();

            maze.set(Maze.ROOM, usedPath);
            maze.set(Maze.ROOM, destRoom);

            MazePath[] neighbors = Maze.getNeighborPaths(position);
            for (MazePath neighbor : neighbors)
            {
                if (maze.get(neighbor) == Maze.NULL)
                {
                    maze.set(Maze.WALL, neighbor);
                }
            }

            position = destRoom;
        }

        for (int i = 0; i < maze.blocks.length; i++)
        {
            if (maze.blocks[i] == Maze.INVALID || maze.blocks[i] == Maze.NULL)
            {
                maze.blocks[i] = Maze.WALL; //Should not happen. Potentially.
            }
        }
    }

    @Deprecated
    public static void addRandomPaths(Maze maze, int paths, Random rand)
    {
        for (int i = 0; i < maze.dimensions.length; i++)
        {
            if ((maze.dimensions[i] - 2) / 2 <= 0)
            {
                return;
            }
        }

        int[] distFromOutside = new int[maze.dimensions.length];
        for (; paths > 0; paths--)
        {
            MazePath position = randomPathInMaze(rand, maze, distFromOutside);
            maze.set(Maze.ROOM, position);
        }
    }

    @Deprecated
    public static void generateStartPathsForEnclosedMaze(Maze maze, MazePath... startPoints)
    {
        for (MazePath path : maze.allPaths())
        {
            if (maze.isPathPointingOutside(path))
            {
                maze.set(Maze.WALL, path);
            }
        }

        for (MazePath startPoint : startPoints)
        {
            maze.set(Maze.ROOM, startPoint);
        }
    }

    public static MazeRoom rotatedRoom(MazeRoom room, AxisAlignedTransform2D transform, int[] size)
    {
        int[] roomPosition = room.coordinates;
        BlockCoord transformedRoom = transform.apply(new BlockCoord(roomPosition[0], roomPosition[1], roomPosition[2]), size);
        return new MazeRoom(transformedRoom.x, transformedRoom.y, transformedRoom.z);
    }

    public static MazePath rotatedPath(MazePath path, AxisAlignedTransform2D transform, int[] size)
    {
        int[] sourceCoords = path.getSourceRoom().coordinates;
        int[] destCoords = path.getDestinationRoom().coordinates;
        BlockCoord transformedSource = transform.apply(new BlockCoord(sourceCoords[0], sourceCoords[1], sourceCoords[2]), size);
        BlockCoord transformedDest = transform.apply(new BlockCoord(destCoords[0], destCoords[1], destCoords[2]), size);

        return MazePath.pathFromSourceAndDest(new MazeRoom(transformedSource.x, transformedSource.y, transformedSource.z), new MazeRoom(transformedDest.x, transformedDest.y, transformedDest.z));
    }
}