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
import ivorius.ivtoolkit.random.WeightedSelector;

import java.util.*;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeGeneratorWithComponents
{
    public static List<MazeComponentPosition> generatePaths(Random rand, final Maze maze, List<MazeComponent> mazeComponents)
    {
        List<MazeComponentPosition> positions = new ArrayList<>();

        Deque<MazeRoom> positionStack = new ArrayDeque<>();

        // Gather needed start points
        for (MazePath path : maze.allPaths())
        {
            if (maze.get(path) == Maze.ROOM)
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
            if (maze.get(position) != Maze.NULL)
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
                        return maze.get(path) == Maze.ROOM;
                    }
                })));

                continue;
            }

            MazeComponentPosition placedComponent = WeightedSelector.canSelect(validComponents)
                    ? WeightedSelector.selectItem(rand, validComponents)
                    : validComponents.get(rand.nextInt(validComponents.size()));

            for (MazeRoom roomInMaze : placedComponent.getRooms())
            {
                // Mark the room
                maze.set(Maze.ROOM, roomInMaze);

                // Prevent exits into the room
                for (MazePath neighbor : MazeCoordinates.getNeighborPaths(roomInMaze))
                    maze.replace(Maze.NULL, Maze.WALL, neighbor);
            }

            for (MazePath exitInMaze : placedComponent.getExitPaths())
            {
                // Mark the exit's paths as to do

                MazeRoom destRoom = exitInMaze.getDestinationRoom();
                if (maze.get(destRoom) == Maze.NULL)
                    positionStack.push(destRoom);

                MazeRoom srcRoom = exitInMaze.getSourceRoom();
                if (maze.get(srcRoom) == Maze.NULL)
                    positionStack.push(srcRoom);

                // Mark the exit
                maze.set(Maze.ROOM, exitInMaze);
            }

            positions.add(placedComponent);
        }

        return positions;
    }

    public static boolean canComponentBePlaced(Maze maze, final MazeComponentPosition component)
    {
        List<MazePath> exitsInMaze = Lists.newArrayList(component.getExitPaths());

        for (MazeRoom room : component.getRooms())
        {
            // Room already taken
            if (maze.get(room) != Maze.NULL)
                return false;

            // Exit is expected where component has none
            for (MazePath roomNeighborPath : MazeCoordinates.getNeighborPaths(room))
                if (maze.get(roomNeighborPath) == Maze.ROOM && !exitsInMaze.contains(roomNeighborPath))
                    return false;
        }

        // No exit is expected where component has one
        for (MazePath exit : exitsInMaze)
            if (maze.get(exit) == Maze.WALL)
                return false;

        return true;
    }
}
