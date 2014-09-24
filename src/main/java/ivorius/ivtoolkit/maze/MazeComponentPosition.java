/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.maze;

import net.minecraft.util.WeightedRandom;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeComponentPosition extends WeightedRandom.Item
{
    private MazeComponent component;

    private MazeRoom positionInMaze;

    public MazeComponentPosition(MazeComponent component, MazeRoom positionInMaze)
    {
        super(component.itemWeight);

        this.component = component;
        this.positionInMaze = positionInMaze;
    }

    public MazeComponent getComponent()
    {
        return component;
    }

    public MazeRoom getPositionInMaze()
    {
        return positionInMaze;
    }
}
