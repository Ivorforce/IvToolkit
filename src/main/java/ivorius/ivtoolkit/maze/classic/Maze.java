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

package ivorius.ivtoolkit.maze.classic;

import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Maze<T>
{

    public final int[] dimensions;
    public final T[] blocks;

    public final T invalidValue;
    public final T nullValue;

    private List<MazeRoom> cachedRooms;
    private List<MazePath> cachedPaths;

    public Maze(Class<T> tClass, T invalidValue, T nullValue, int... dimensions)
    {
        int fullLength = 1;
        for (int dimension : dimensions)
        {
            if (dimension % 2 == 0)
                throw new IllegalArgumentException("Maze must have enclosing walls! (Odd dimension numbers)");

            fullLength *= dimension;
        }

        this.invalidValue = invalidValue;
        this.nullValue = null;

        this.dimensions = dimensions;

        this.blocks = (T[]) Array.newInstance(tClass, fullLength);
        if (nullValue != null)
            Arrays.fill(blocks, nullValue);
    }

    public boolean contains(MazeCoordinate coordinate)
    {
        int[] position = coordinate.getMazeCoordinates();

        for (int i = 0; i < position.length; i++)
        {
            if (position[i] < 0 || position[i] >= this.dimensions[i])
                return false;
        }

        return true;
    }

    public T get(MazeCoordinate coordinate)
    {
        return contains(coordinate) ? blocks[getArrayPosition(coordinate.getMazeCoordinates())] : invalidValue;
    }

    public void set(T val, MazeCoordinate coordinates)
    {
        if (contains(coordinates))
            blocks[getArrayPosition(coordinates.getMazeCoordinates())] = val;
    }

    public void replace(T condition, T val, MazeCoordinate coordinates)
    {
        if (contains(coordinates))
        {
            int arrayPosition = getArrayPosition(coordinates.getMazeCoordinates());
            if (Objects.equals(blocks[arrayPosition], condition))
                blocks[arrayPosition] = val;
        }
    }

    public boolean isInvalid(T value)
    {
        return Objects.equals(value, invalidValue);
    }

    public boolean isNull(T value)
    {
        return Objects.equals(value, nullValue);
    }

    public boolean isInvalidOrNull(T value)
    {
        return isNull(value) || isInvalid(value);
    }

    public static int[] getMazeSize(int size[], int pathLengths[], int roomSize[])
    {
        int[] returnSize = new int[size.length];

        for (int i = 0; i < returnSize.length; i++)
            returnSize[i] = (size[i] - pathLengths[i]) / (pathLengths[i] + roomSize[i]) * 2 + 1;

        return returnSize;
    }

    public static int[] getRoomPosition(MazeCoordinate coordinate, int[] pathLengths, int[] roomSize)
    {
        int[] mazePosition = coordinate.getMazeCoordinates();
        int[] returnPos = new int[pathLengths.length];

        for (int i = 0; i < returnPos.length; i++)
            returnPos[i] = (mazePosition[i] / 2) * roomSize[i] + ((mazePosition[i] + 1) / 2) * pathLengths[i];

        return returnPos;
    }

    public static int[] getRoomSize(int[] rooms, int[] pathLengths, int[] roomSize)
    {
        int[] returnSize = new int[pathLengths.length];

        for (int i = 0; i < returnSize.length; i++)
            returnSize[i] = rooms[i] * roomSize[i] + (rooms[i] / 2) * pathLengths[i];

        return returnSize;
    }

    public int[] getCompleteMazeSize(int[] pathLengths, int[] roomWidths)
    {
        return getRoomPosition(new MazeRoom(this.dimensions), pathLengths, roomWidths);
    }

    public int[] getRoomSize(MazeCoordinate coordinate, int[] pathLengths, int[] roomWidths)
    {
        int[] returnSize = new int[this.dimensions.length];
        boolean[] isRoomPath = MazeCoordinates.coordPathFlags(coordinate);

        for (int i = 0; i < returnSize.length; i++)
            returnSize[i] = isRoomPath[i] ? pathLengths[i] : roomWidths[i];

        return returnSize;
    }

    public static boolean isCoordValidRoom(MazeCoordinate coordinate)
    {
        boolean[] isRoomPath = MazeCoordinates.coordPathFlags(coordinate);

        for (boolean b : isRoomPath)
            if (b)
                return false;

        return true;
    }

    public static int getPathDimensionIfPath(MazeCoordinate coordinate)
    {
        boolean[] pathFlags = MazeCoordinates.coordPathFlags(coordinate);
        int curDimension = -1;

        for (int dim = 0; dim < pathFlags.length; dim++)
        {
            if (pathFlags[dim])
            {
                if (curDimension >= 0)
                    return -1;

                curDimension = dim;
            }
        }

        return curDimension;
    }

    public boolean isPathPointingOutside(MazeCoordinate coordinate)
    {
        int[] mazePosition = coordinate.getMazeCoordinates();

        for (int dim = 0; dim < dimensions.length; dim++)
            if (mazePosition[dim] == 0 || mazePosition[dim] == dimensions[dim] - 1)
                return true;

        return false;
    }

    public int[] getCoordPosition(int arrayPosition)
    {
        int[] coordPosition = new int[this.dimensions.length];

        for (int dimension = 0; dimension < this.dimensions.length; dimension++)
        {
            coordPosition[dimension] = arrayPosition % this.dimensions[dimension];
            arrayPosition /= this.dimensions[dimension];
        }

        return coordPosition;
    }

    public int getArrayPosition(int... coordPosition)
    {
        int arrayPosition = 0;

        int multiplier = 1;
        for (int dimension = 0; dimension < this.dimensions.length; dimension++)
        {
            arrayPosition += coordPosition[dimension] * multiplier;
            multiplier *= this.dimensions[dimension];
        }

        return arrayPosition;
    }

    public void logMaze2D(Logger logger, int dimension1, int dimension2, MazeCoordinate layerPositon)
    {
        int[] layerPositionArray = layerPositon.getMazeCoordinates();
        StringBuilder mazeString = new StringBuilder();

        for (int dim = 0; dim < dimensions.length; dim++)
            if (dim != dimension1 && dim != dimension2 && (dim < 0 || layerPositionArray[dim] >= dimensions[dim]))
                throw new IllegalArgumentException();

        for (int x = 0; x < this.dimensions[dimension1]; x++)
        {
            if (x > 0)
                mazeString.append("\n");

            for (int y = 0; y < this.dimensions[dimension2]; y++)
            {
                layerPositionArray[dimension1] = x;
                layerPositionArray[dimension2] = y;
                T type = this.blocks[getArrayPosition(layerPositionArray)];
                mazeString.append(type.toString());

//                switch (type)
//                {
//                    case WALL:
//                        mazeString.append("#");
//                        break;
//                    case INVALID:
//                        mazeString.append("*");
//                        break;
//                    case NULL:
//                        mazeString.append("+");
//                        break;
//                    case ROOM:
//                        if (this.dimensions.length > 2)
//                        {
//                            int curDominantDimension = -1;
//                            boolean dominantGoesUp = false;
//
//                            for (int refDim = 0; refDim < dimensions.length; refDim++)
//                            {
//                                if (refDim != dimension1 && refDim != dimension2)
//                                {
//                                    int[] abovePos = layerPositionArray.clone();
//                                    abovePos[refDim] += 1;
//                                    int[] belowPos = layerPositionArray.clone();
//                                    belowPos[refDim] -= 1;
//
//                                    byte above = get(new MazeCoordinateDirect(abovePos));
//                                    byte below = get(new MazeCoordinateDirect(belowPos));
//
//                                    if (above == ROOM || below == ROOM)
//                                    {
//                                        if (curDominantDimension >= 0 || above == below)
//                                        {
//                                            curDominantDimension = -2;
//                                            break;
//                                        }
//                                        else
//                                        {
//                                            curDominantDimension = refDim;
//                                            dominantGoesUp = above == ROOM;
//                                        }
//                                    }
//                                }
//                            }
//
//                            switch (curDominantDimension)
//                            {
//                                case -2:
//                                    mazeString.append('?');
//                                    break;
//                                case -1:
//                                    mazeString.append(' ');
//                                    break;
//                                default:
//                                    mazeString.append((char) ((dominantGoesUp ? 'A' : 'a') + curDominantDimension));
//                                    break;
//                            }
//                        }
//                        break;
//                    default:
//                        mazeString.append(" ");
//                        break;
//                }
            }
        }

        logger.info(mazeString.toString());
    }

    public List<MazeRoom> allRooms()
    {
        if (cachedRooms == null)
        {
            List<MazeRoom> coordinates = new ArrayList<>();

            for (int i = 0; i < blocks.length; i++)
            {
                int[] coord = getCoordPosition(i);
                MazeRoom room = MazeCoordinates.coordToRoom(new MazeCoordinateDirect(coord));

                if (room != null)
                    coordinates.add(room);
            }

            cachedRooms = coordinates;
        }

        return cachedRooms;
    }

    public List<MazePath> allPaths()
    {
        if (cachedPaths == null)
        {
            List<MazePath> coordinates = new ArrayList<>();

            for (int i = 0; i < blocks.length; i++)
            {
                int[] coord = getCoordPosition(i);
                MazePath path = MazeCoordinates.coordToPath(new MazeCoordinateDirect(coord));

                if (path != null)
                    coordinates.add(path);
            }

            cachedPaths = coordinates;
        }

        return cachedPaths;
    }
}