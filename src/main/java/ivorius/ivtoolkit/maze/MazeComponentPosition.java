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

import ivorius.ivtoolkit.random.WeightedSelector;
import net.minecraft.util.WeightedRandom;

/**
 * Created by lukas on 20.06.14.
 */
public class MazeComponentPosition extends WeightedRandom.Item implements WeightedSelector.Item
{
    protected MazeComponent component;

    protected MazeRoom positionInMaze;

    protected double weight;

    public MazeComponentPosition(MazeComponent component, MazeRoom positionInMaze)
    {
        super(component.itemWeight);
        weight = component.weight;

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

    @Override
    public double getWeight()
    {
        return weight;
    }
}
