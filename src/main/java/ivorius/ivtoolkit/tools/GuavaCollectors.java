/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.tools;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.UNORDERED;

/**
 * Created by lukas on 24.03.16.
 */
public class GuavaCollectors
{
    /**
     * Collect a stream of elements into an {@link ImmutableList}.
     */
    public static <T> Collector<T, ImmutableList.Builder<T>, ImmutableList<T>> immutableList()
    {
        return Collector.of(ImmutableList.Builder<T>::new,
                ImmutableList.Builder<T>::add,
                (l, r) -> l.addAll(r.build()),
                ImmutableList.Builder<T>::build);
    }

    /**
     * Collect a stream of elements into an {@link ImmutableSet}.
     */
    public static <T> Collector<T, ImmutableSet.Builder<T>, ImmutableSet<T>> immutableSet()
    {
        return Collector.of(ImmutableSet.Builder<T>::new,
                ImmutableSet.Builder<T>::add,
                (l, r) -> l.addAll(r.build()),
                ImmutableSet.Builder<T>::build, UNORDERED);
    }

    /**
     * Collect a stream of elements into an {@link ImmutableMap}.
     */
    public static <K, U> Collector<K, ?, ImmutableMap<K, U>> toMap(Function<? super K, ? extends U> valueMapper)
    {
        return toMap(Function.identity(), valueMapper);
    }

    /**
     * Collect a stream of elements into an {@link ImmutableMap}.
     */
    public static <T, K, U> Collector<T, ?, ImmutableMap<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper)
    {
        return Collector.of(ImmutableMap.Builder<K, U>::new,
                (r, t) -> r.put(keyMapper.apply(t), valueMapper.apply(t)),
                (l, r) -> l.putAll(r.build()),
                ImmutableMap.Builder::build, UNORDERED);
    }

    /**
     * Collect a stream of elements into an {@link ImmutableMap}.
     */
    public static <K, U> Collector<K, ?, ImmutableMultimap<K, U>> toMultimap(Function<? super K, Iterable<? extends U>> valueMapper)
    {
        return toMultimap(Function.identity(), valueMapper);
    }

    /**
     * Collect a stream of elements into an {@link ImmutableMap}.
     */
    public static <T, K, U> Collector<T, ?, ImmutableMultimap<K, U>> toMultimap(Function<? super T, ? extends K> keyMapper, Function<? super T, Iterable<? extends U>> valueMapper)
    {
        return Collector.of(ImmutableMultimap.Builder<K, U>::new,
                (r, t) -> r.putAll(keyMapper.apply(t), valueMapper.apply(t)),
                (l, r) -> l.putAll(r.build()),
                ImmutableMultimap.Builder::build, UNORDERED);
    }
}
