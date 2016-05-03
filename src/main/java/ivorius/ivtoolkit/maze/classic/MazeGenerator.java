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

import com.google.common.collect.Lists;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;

import java.util.*;

public class MazeGenerator
{
    public static <T> void blockRoomAndExits(Maze<T> maze, MazeRoom coordinate, T room, T blockedPath)
    {
        maze.set(room, coordinate);

        for (MazePath neighbor : MazeCoordinates.getNeighborPaths(coordinate))
            maze.replace(maze.nullValue, blockedPath, neighbor);
    }

    public static <T> void generateStartPathsForEnclosedMaze(Maze<T> maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform, T path, T room, T blockedPath)
    {
        int[] mazeSizeInRooms = new int[maze.dimensions.length];
        for (int i = 0; i < mazeSizeInRooms.length; i++)
            mazeSizeInRooms[i] = (maze.dimensions[i] - 1) / 2;

        maze.allPaths().stream().filter(maze::isPathPointingOutside).forEach(exitPath -> maze.set(blockedPath, exitPath));

        for (MazeRoom blockedRoom : blockedRooms)
        {
            blockedRoom = MazeCoordinates.rotatedRoom(blockedRoom, transform, mazeSizeInRooms);
            blockRoomAndExits(maze, blockedRoom, room, blockedPath);
        }

        for (MazePath startPoint : startPoints)
            maze.set(path, MazeCoordinates.rotatedPath(startPoint, transform, mazeSizeInRooms));
    }

    public static <T> MazePath randomEmptyPathInMaze(Random rand, Maze<T> maze, Collection<Integer> applicableDimensions)
    {
        List<MazePath> paths = Lists.newArrayList(maze.allPaths());
        for (Iterator<MazePath> iterator = paths.iterator(); iterator.hasNext(); )
        {
            MazePath path = iterator.next();
            if (!maze.isNull(maze.get(path)) || !applicableDimensions.contains(path.getPathDimension()))
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
            position[i] = rand.nextInt(maze.dimensions[i] / 2 - distanceFromOutside[i]);

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

        return MazeCoordinates.coordToPath(new MazeCoordinateDirect(pathCoord), usedDimension);
    }
}