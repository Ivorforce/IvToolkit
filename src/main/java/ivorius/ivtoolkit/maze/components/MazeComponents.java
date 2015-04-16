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
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    public static <M extends MazeComponent<C>, C> Function<M, Iterable<ShiftedMazeComponent<M, C>>> shiftAllFunction(final MazeRoomConnection connection, final C connector, final ConnectionStrategy<C> strategy)
    {
        return new Function<M, Iterable<ShiftedMazeComponent<M, C>>>()
        {
            @Nullable
            @Override
            public Iterable<ShiftedMazeComponent<M, C>> apply(@Nullable M input)
            {
                Set<ShiftedMazeComponent<M, C>> set = new HashSet<>();

                if (input != null)
                {
                    for (Map.Entry<MazeRoomConnection, C> entry : input.exits().entrySet())
                    {
                        MazeRoom dist = entry.getKey().distance(connection);
                        if (dist != null) set.add(shift(input, dist));
                    }
                }

                return set;
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

    public static <C> boolean componentsCompatible(final MazeComponent<C> existing, final MazeComponent<C> add, final ConnectionStrategy<C> strategy)
    {
        return !overlap(existing, add) && allExitsCompatible(existing, add, strategy);
    }

    public static boolean overlap(MazeComponent<?> left, MazeComponent<?> right)
    {
        return Sets.intersection(left.rooms(), right.rooms()).size() > 0;
    }

    public static <C> boolean allExitsCompatible(final MazeComponent<C> existing, final MazeComponent<C> add, final ConnectionStrategy<C> strategy)
    {
        return Iterables.all(add.exits().entrySet(), new Predicate<Map.Entry<MazeRoomConnection, C>>()
        {
            @Override
            public boolean apply(@Nullable Map.Entry<MazeRoomConnection, C> input)
            {
                return input == null || strategy.connect(input.getKey(), existing.exits().get(input.getKey()), input.getValue());
            }
        });
    }
}
