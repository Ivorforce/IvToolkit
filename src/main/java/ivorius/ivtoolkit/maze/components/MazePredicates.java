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

package ivorius.ivtoolkit.maze.components;

import com.google.common.base.Predicate;

/**
 * Created by lukas on 15.04.15.
 */
public class MazePredicates
{
    public static <M extends MazeComponent<C>, C> Predicate<ShiftedMazeComponent<M, C>> placeable(final MorphingMazeComponent<C> maze, final MazePredicate<M, C> strategy)
    {
        return new Predicate<ShiftedMazeComponent<M, C>>()
        {
            @Override
            public boolean apply(ShiftedMazeComponent<M, C> input)
            {
                return strategy.canPlace(maze, input);
            }
        };
    }
}
