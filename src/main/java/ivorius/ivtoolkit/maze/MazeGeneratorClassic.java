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

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;

import java.util.*;

public class MazeGeneratorClassic
{
    public static final byte NULL = 0;
    public static final byte INVALID = 1;
    public static final byte WALL = 2;
    public static final byte ROOM = 3;

    public static Maze<Byte> initialize(int... dimensions)
    {
        return new Maze<>(Byte.class, INVALID, NULL, dimensions);
    }

    public static void generateStartPathsForEnclosedMaze(Maze<Byte> maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform)
    {
        MazeGenerator.generateStartPathsForEnclosedMaze(maze, startPoints, blockedRooms, transform, ROOM, ROOM, WALL);
    }

    public static void generatePaths(Random rand, Maze<Byte> maze, int[] pathWeights, MazeRoom startPoint)
    {
        for (int i = 0; i < maze.dimensions.length; i++)
            if (maze.dimensions[i] < 3)
                return;

        MazeRoom position = startPoint;
        maze.set(ROOM, position);

        Stack<MazeRoom> positionStack = new Stack<>();

        ArrayList<MazePath> validRoomNeighbors = new ArrayList<>();

        while (true)
        {
            validRoomNeighbors.clear();

            for (MazePath neighbor : MazeCoordinates.getNeighborPaths(position))
            {
                if (maze.isNull(maze.get(neighbor.getDestinationRoom())))
                {
                    for (int n = 0; n < pathWeights[neighbor.getPathDimension()]; n++)
                        validRoomNeighbors.add(neighbor);
                }
            }

            if (validRoomNeighbors.size() == 0)
            {
                if (positionStack.empty())
                    break;

                position = positionStack.pop();

                continue;
            }

            positionStack.push(position);

            MazePath usedPath = validRoomNeighbors.get(rand.nextInt(validRoomNeighbors.size()));
            MazeRoom destRoom = usedPath.getDestinationRoom();

            maze.set(ROOM, usedPath);
            maze.set(ROOM, destRoom);

            MazePath[] neighbors = MazeCoordinates.getNeighborPaths(position);
            for (MazePath neighbor : neighbors)
                if (maze.isInvalid(maze.get(neighbor)))
                    maze.set(WALL, neighbor);

            position = destRoom;
        }

        for (int i = 0; i < maze.blocks.length; i++)
            if (maze.isInvalidOrNull(maze.blocks[i]))
                maze.blocks[i] = WALL; //Should not happen. Potentially.
    }
}