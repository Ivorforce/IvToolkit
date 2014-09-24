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

package ivorius.ivtoolkit.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 22.06.14.
 */
public class IvCollections
{
    public static <O> ArrayList<O> modifiableCopy(List<O> list)
    {
        ArrayList<O> newList = new ArrayList<>();
        newList.addAll(list);
        return newList;
    }

    public static <O> ArrayList<O> modifiableCopyWithout(List<O> list, int... removeIndices)
    {
        int[] newIndices = removeIndices.clone();
        Arrays.sort(removeIndices);

        ArrayList<O> newList = modifiableCopy(list);

        for (int i = newIndices.length - 1; i >= 0; i--)
        {
            newList.remove(newIndices[i]);
        }

        return newList;
    }

    @SafeVarargs
    public static <O> ArrayList<O> modifiableCopyWith(List<O> list, O... objects)
    {
        ArrayList<O> newList = modifiableCopy(list);
        Collections.addAll(newList, objects);
        return newList;
    }

    public static <O> void setContentsOfList(List<O> list, List<O> contents)
    {
        list.clear();
        list.addAll(contents);
    }
}
