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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.*;

public class WeightedSelector
{
    public static <T extends Item> double totalWeight(Collection<T> items)
    {
        double totalWeight = 0.0;
        for (T t : items)
            totalWeight += t.getWeight();
        return totalWeight;
    }

    public static <T> double totalWeight(Collection<T> items, final WeightFunction<T> weightFunction)
    {
        return totalWeight(SimpleItem.apply(items, weightFunction));
    }

    public static boolean canSelect(Collection<? extends Item> items)
    {
        for (Item item : items)
            if (item.getWeight() > 0)
                return true;
        return false;
    }

    public static <T> boolean canSelect(Collection<T> items, WeightFunction<T> weightFunction)
    {
        for (T item : items)
            if (weightFunction.apply(item) > 0)
                return true;
        return false;
    }

    public static <T extends Item> T selectItem(Random rand, Collection<T> items)
    {
        return selectItem(rand, items, totalWeight(items));
    }

    public static <T extends Item> T selectItem(Random rand, Collection<T> items, double totalWeight)
    {
        return selectItem(rand, items, totalWeight, false);
    }

    public static <T extends Item> T selectItem(Random rand, Collection<T> items, double totalWeight, boolean remove)
    {
        if (items.size() == 0)
            throw new IndexOutOfBoundsException();

        double random = rand.nextDouble() * totalWeight;
        T last = null;
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); )
        {
            T t = iterator.next();
            last = t;
            random -= t.getWeight();
            if (random <= 0.0)
            {
                if (remove)
                    iterator.remove();
                return t;
            }
        }

        return last;
    }

    public static <T> T select(Random rand, Collection<T> items, final WeightFunction<T> weightFunction)
    {
        return select(rand, SimpleItem.apply(items, weightFunction));
    }

    public static <T> T select(Random rand, Collection<T> items, final WeightFunction<T> weightFunction, double totalWeight)
    {
        return select(rand, SimpleItem.apply(items, weightFunction), totalWeight);
    }

    public static <T> T select(Random rand, Collection<SimpleItem<T>> items)
    {
        return select(rand, items, totalWeight(items));
    }

    public static <T> T select(Random rand, Collection<SimpleItem<T>> items, double totalWeight)
    {
        return selectItem(rand, items, totalWeight).getItem();
    }

    public interface Item
    {
        double getWeight();
    }

    public static class SimpleItem<T> implements Item, Comparable<Item>
    {
        protected final double weight;
        protected final T item;

        public SimpleItem(double weight, T item)
        {
            this.item = item;
            this.weight = weight;
        }

        public static <T> SimpleItem<T> of(double weight, T item)
        {
            return new SimpleItem<>(weight, item);
        }

        public static <T> Collection<SimpleItem<T>> apply(Collection<T> items, final WeightFunction<T> weightFunction)
        {
            return Collections2.transform(items, item -> new SimpleItem<>(weightFunction.apply(item), item));
        }

        public static <T> List<SimpleItem<T>> apply(List<T> items, final WeightFunction<T> weightFunction)
        {
            return Lists.transform(items, input -> new SimpleItem<>(weightFunction.apply(input), input));
        }

        public T getItem()
        {
            return item;
        }

        @Override
        public double getWeight()
        {
            return weight;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleItem that = (SimpleItem) o;

            if (Double.compare(that.weight, weight) != 0) return false;
            if (item != null ? !item.equals(that.item) : that.item != null) return false;

            return true;
        }

        @Override
        public int hashCode()
        {
            int result;
            long temp;
            temp = Double.doubleToLongBits(weight);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (item != null ? item.hashCode() : 0);
            return result;
        }

        @Override
        public String toString()
        {
            return "SimpleItem{" +
                    "weight=" + weight +
                    ", item=" + item +
                    '}';
        }

        @Override
        public int compareTo(Item o)
        {
            return Double.compare(weight, o.getWeight());
        }
    }

    public interface WeightFunction<T>
    {
        double apply(T item);
    }

    public static class ItemComparator implements Comparator<Item>
    {
        @Override
        public int compare(Item o1, Item o2)
        {
            return Double.compare(o1.getWeight(), o2.getWeight());
        }
    }
}

