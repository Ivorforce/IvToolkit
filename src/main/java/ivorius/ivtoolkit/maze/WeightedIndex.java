/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.maze;

import net.minecraft.util.WeightedRandom;

/**
 * Created by lukas on 24.06.14.
 */
public class WeightedIndex extends WeightedRandom.Item
{
    private int index;

    public WeightedIndex(int weight, int index)
    {
        super(weight);
        this.index = index;
    }

    public int getIndex()
    {
        return index;
    }
}
