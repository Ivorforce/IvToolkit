/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.maze;

/**
 * Created by lukas on 23.06.14.
 */
public class MazeCoordinateDirect implements MazeCoordinate
{
    public int[] coordinates;

    public MazeCoordinateDirect(int... coordinates)
    {
        this.coordinates = coordinates;
    }

    @Override
    public int[] getMazeCoordinates()
    {
        return coordinates;
    }
}
