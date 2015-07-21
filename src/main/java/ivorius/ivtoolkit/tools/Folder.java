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

package ivorius.ivtoolkit.tools;

import java.util.Iterator;

/**
 * Created by lukas on 21.07.15.
 */
public class Folder
{
    public static <T> T fold(Iterable<T> ts, Reducer<T> reducer, T initial)
    {
        for (T t : ts)
            initial = reducer.reduce(initial, t);
        return initial;
    }

    public static <T> T fold(Iterable<T> ts, Reducer<T> reducer)
    {
        Iterator<T> iterator = ts.iterator();

        T initial = iterator.next();
        for (; iterator.hasNext(); )
        {
            T t = iterator.next();
            initial = reducer.reduce(initial, t);
        }

        return initial;
    }

    public interface Reducer<T>
    {
        T reduce(T left, T right);
    }
}
