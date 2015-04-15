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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;

import java.util.*;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeGeneratorWithComponents
{
    private static final Entry INVALID = new Entry();

    private static final BlockedPath BLOCKED_PATH = new BlockedPath();
    private static final Room ROOM = new Room();
    private static final Path PATH = new Path();

    public static Maze<Entry> initialize(int... dimensions)
    {
        return new Maze<>(Entry.class, INVALID, null, dimensions);
    }

    public static void generateStartPathsForEnclosedMaze(Maze<Entry> maze, Iterable<MazePath> startPoints, Iterable<MazeRoom> blockedRooms, AxisAlignedTransform2D transform)
    {
        MazeGenerator.generateStartPathsForEnclosedMaze(maze, startPoints, blockedRooms, transform, PATH, ROOM, BLOCKED_PATH);
    }

    public static List<MazeComponentPosition> generatePaths(Random rand, final Maze<Entry> maze, List<MazeComponent> mazeComponents)
    {
        List<MazeComponentPosition> positions = new ArrayList<>();

        Deque<MazeRoom> positionStack = new ArrayDeque<>();

        // Gather needed start points
        for (MazePath path : maze.allPaths())
        {
            if (maze.get(path) instanceof Path)
            {
                positionStack.push(path.getSourceRoom());
                positionStack.push(path.getDestinationRoom());
            }
        }

        List<MazeComponentPosition> validComponents = new ArrayList<>();

        while (!positionStack.isEmpty())
        {
            MazeRoom position = positionStack.pop();

            // Has been filled while this was queued ?
            if (!maze.isNull(maze.get(position)))
                continue;

            validComponents.clear();

            // Find valid components
            for (MazeComponent component : mazeComponents)
                for (MazeRoom attachedRoom : component.getRooms())
                {
                    MazeRoom componentPosition = position.sub(attachedRoom);

                    if (canComponentBePlaced(maze, new MazeComponentPosition(component, componentPosition)))
                        validComponents.add(new MazeComponentPosition(component, componentPosition));
                }

            if (validComponents.size() == 0)
            {
                System.out.println("Did not find fitting component for maze!");
                System.out.println("Suggested: X with exits " + Lists.newArrayList(Iterables.filter(Arrays.asList(MazeCoordinates.getNeighborPaths(position)), new Predicate<MazePath>()
                {
                    @Override
                    public boolean apply(MazePath path)
                    {
                        return maze.get(path) instanceof Path;
                    }
                })));

                continue;
            }

            MazeComponentPosition placedComponent = WeightedSelector.canSelect(validComponents)
                    ? WeightedSelector.selectItem(rand, validComponents)
                    : validComponents.get(rand.nextInt(validComponents.size()));

            for (MazeRoom roomInMaze : placedComponent.getRooms())
                MazeGenerator.blockRoomAndExits(maze, roomInMaze, ROOM, BLOCKED_PATH);

            for (MazePath exitInMaze : placedComponent.getExitPaths())
            {
                // Mark the exit's paths as to do

                MazeRoom destRoom = exitInMaze.getDestinationRoom();
                if (maze.isNull(maze.get(destRoom)))
                    positionStack.push(destRoom);

                MazeRoom srcRoom = exitInMaze.getSourceRoom();
                if (maze.isNull(maze.get(srcRoom)))
                    positionStack.push(srcRoom);

                // Mark the exit
                maze.set(PATH, exitInMaze);
            }

            positions.add(placedComponent);
        }

        return positions;
    }

    public static boolean canComponentBePlaced(Maze<Entry> maze, final MazeComponentPosition component)
    {
        List<MazePath> exitsInMaze = Lists.newArrayList(component.getExitPaths());

        for (MazeRoom room : component.getRooms())
        {
            // Room already taken
            if (!maze.isNull(maze.get(room)))
                return false;

            // Exit is expected where component has none
            for (MazePath roomNeighborPath : MazeCoordinates.getNeighborPaths(room))
                if (maze.get(roomNeighborPath) instanceof Path && !exitsInMaze.contains(roomNeighborPath))
                    return false;
        }

        // No exit is expected where component has one
        for (MazePath exit : exitsInMaze)
            if (maze.get(exit) instanceof BlockedPath)
                return false;

        return true;
    }

    public static class Entry
    {

    }

    public static class Path extends Entry
    {

    }

    public static class Room extends Entry
    {

    }

    public static class BlockedPath extends Entry
    {

    }
}
