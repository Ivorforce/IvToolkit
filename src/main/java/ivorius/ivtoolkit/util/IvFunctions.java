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

package ivorius.ivtoolkit.util;

import com.google.common.collect.ImmutableMultimap;
import ivorius.ivtoolkit.tools.GuavaCollectors;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Created by lukas on 19.11.16.
 */
public class IvFunctions
{
    public static <T> Collection<Collection<T>> group(List<T> list, Function<T, Object> group)
    {
        return groupMap(list, group).asMap().values();
    }

    public static <K, T> ImmutableMultimap<K, T> groupMap(List<T> list, Function<T, K> group)
    {
        return list.stream().collect(GuavaCollectors.toMultimap(group, Collections::singleton));
    }
}
