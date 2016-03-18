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
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 30.09.15.
 */
public class MazePredicateMany<M extends MazeComponent<C>, C> implements MazePredicate<M, C>
{
    public final List<MazePredicate<M, C>> predicates = new ArrayList<>();

    public MazePredicateMany()
    {
    }

    public MazePredicateMany(List<MazePredicate<M, C>> predicates)
    {
        this.predicates.addAll(predicates);
    }

    @SafeVarargs
    public MazePredicateMany(MazePredicate<M, C>... predicates)
    {
        Collections.addAll(this.predicates, predicates);
    }

    @Override
    public boolean canPlace(final MorphingMazeComponent<C> maze, final ShiftedMazeComponent<M, C> component)
    {
        return Iterables.all(predicates, new Predicate<MazePredicate<M, C>>()
        {
            @Override
            public boolean apply(MazePredicate<M, C> p)
            {
                return p.canPlace(maze, component);
            }
        });
    }

    @Override
    public void willPlace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<M, C> component)
    {
        for (MazePredicate<M, C> predicate : predicates)
            predicate.willPlace(maze, component);
    }

    @Override
    public void didUnplace(MorphingMazeComponent<C> maze, ShiftedMazeComponent<M, C> component)
    {
        for (MazePredicate<M, C> predicate : predicates)
            predicate.didUnplace(maze, component);
    }

    @Override
    public boolean isDirtyConnection(final MazeRoom dest, final MazeRoom source, final C c)
    {
        return Iterables.all(predicates, new Predicate<MazePredicate<M, C>>()
        {
            @Override
            public boolean apply(MazePredicate<M, C> p)
            {
                return p.isDirtyConnection(dest, source, c);
            }
        });
    }
}
