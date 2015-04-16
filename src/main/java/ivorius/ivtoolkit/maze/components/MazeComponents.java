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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;

/**
 * Created by lukas on 15.04.15.
 */
public class MazeComponents
{
    public static <M extends MazeComponent<C>, C> Function<M, ShiftedMazeComponent<M, C>> shiftFunction(final MazeRoom shift)
    {
        return new Function<M, ShiftedMazeComponent<M, C>>()
        {
            @Nullable
            @Override
            public ShiftedMazeComponent<M, C> apply(@Nullable M input)
            {
                return shift(input, shift);
            }
        };
    }

    public static <M extends MazeComponent<C>, C> ShiftedMazeComponent<M, C> shift(final M component, final MazeRoom shift)
    {
        return new ShiftedMazeComponent<>(component, shift, FluentIterable.from(component.rooms()).transform(new Function<MazeRoom, MazeRoom>()
        {
            @Nullable
            @Override
            public MazeRoom apply(MazeRoom input)
            {
                return input != null ? input.add(shift) : null;
            }
        }).toSet(), FluentIterable.from(component.exits().keySet()).transform(new Function<MazeRoomConnection, MazeRoomConnection>()
        {
            @Nullable
            @Override
            public MazeRoomConnection apply(@Nullable MazeRoomConnection input)
            {
                return input != null ? input.add(shift) : null;
            }
        }).toMap(new Function<MazeRoomConnection, C>()
        {
            @Nullable
            @Override
            public C apply(@Nullable MazeRoomConnection input)
            {
                return component.exits().get(input != null ? input.sub(shift) : null);
            }
        }));
    }

    public static <M extends MazeComponent<C>, C> Predicate<ShiftedMazeComponent<M, C>> compatibilityPredicate(final MazeComponent<C> component, final ConnectionStrategy<C> strategy)
    {
        return new Predicate<ShiftedMazeComponent<M, C>>()
        {
            @Override
            public boolean apply(@Nullable ShiftedMazeComponent<M, C> input)
            {
                return componentsCompatible(component, input, strategy);
            }
        };
    }

    public static <C> boolean componentsCompatible(final MazeComponent<C> left, final MazeComponent<C> right, final ConnectionStrategy<C> strategy)
    {
        return !overlap(left, right) && allExitsCompatible(left, right, strategy);
    }

    public static boolean overlap(MazeComponent<?> left, MazeComponent<?> right)
    {
        return Sets.intersection(left.rooms(), right.rooms()).size() > 0;
    }

    public static <C> boolean allExitsCompatible(final MazeComponent<C> left, final MazeComponent<C> right, final ConnectionStrategy<C> strategy)
    {
        return Iterables.all(Sets.union(left.exits().keySet(), right.exits().keySet()), new Predicate<MazeRoomConnection>()
        {
            @Override
            public boolean apply(@Nullable MazeRoomConnection input)
            {
                return input == null || strategy.connect(input, left.exits().get(input), right.exits().get(input));
            }
        });
    }
}
