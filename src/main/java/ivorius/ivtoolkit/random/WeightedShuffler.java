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

package ivorius.ivtoolkit.random;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.ToDoubleFunction;

public class WeightedShuffler
{
    public static <T> Iterator<T> iterateShuffled(Random rand, List<T> items, ToDoubleFunction<T> weightFunction)
    {
        return new Iterator<T>()
        {
            double totalWeight = WeightedSelector.totalWeight(items, weightFunction);

            @Override
            public boolean hasNext()
            {
                return items.size() > 0;
            }

            @Override
            public T next()
            {
                T next = totalWeight > 0.0000001
                        ? WeightedSelector.select(rand, items, weightFunction, totalWeight, true)
                        : items.remove(rand.nextInt(items.size()));
                totalWeight -= weightFunction.applyAsDouble(next);
                return next;
            }
        };
    }

    public static <T> Iterator<T> iterateShuffled(Random rand, List<WeightedSelector.SimpleItem<T>> items)
    {
        Iterator<WeightedSelector.SimpleItem<T>> iterator = iterateShuffled(rand, items, WeightedSelector.Item::getWeight);
        return new Iterator<T>()
        {
            @Override
            public boolean hasNext()
            {
                return iterator.hasNext();
            }

            @Override
            public T next()
            {
                return iterator.next().getItem();
            }
        };
    }

    public static <T extends WeightedSelector.Item> Iterator<T> iterateShuffledItems(Random rand, List<T> items)
    {
        return iterateShuffled(rand, items, WeightedSelector.Item::getWeight);
    }

    public static <T extends WeightedSelector.Item> void shuffleItems(Random rand, List<T> items)
    {
        shuffleItems(rand, items, WeightedSelector.totalWeight(items));
    }

    public static <T extends WeightedSelector.Item> void shuffleItems(Random rand, List<T> items, double totalWeight)
    {
        int size = items.size();

        for (int shuffled = 0; shuffled < size - 1; shuffled++)
        {
            List<T> subList = items.subList(shuffled, size);
            T selected = totalWeight > 0 ? WeightedSelector.selectItem(rand, subList, totalWeight, true) : subList.get(rand.nextInt(subList.size()));
            totalWeight -= selected.getWeight();
            subList.add(0, selected);
        }
    }

    public static <T> void shuffle(Random rand, List<T> items, ToDoubleFunction<T> weightFunction)
    {
        shuffle(rand, items, weightFunction, WeightedSelector.totalWeight(items, weightFunction));
    }

    public static <T> void shuffle(Random rand, List<T> items, ToDoubleFunction<T> weightFunction, double totalWeight)
    {
        int size = items.size();

        for (int shuffled = 0; shuffled < size - 1; shuffled++)
        {
            List<T> subList = items.subList(shuffled, size);
            T selected = totalWeight > 0 ? WeightedSelector.select(rand, subList, weightFunction, totalWeight, true) : subList.get(rand.nextInt(subList.size()));
            totalWeight -= weightFunction.applyAsDouble(selected);
            subList.add(0, selected);
        }
    }
}

