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

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

public class WeightedSelector
{
    public static <T extends Item> T selectItem(Random rand, Collection<T> items)
    {
        double totalWeight = 0.0;
        for (T t : items)
            totalWeight += t.getWeight();
        return selectItem(rand, items, totalWeight);
    }

    public static <T extends Item> T selectItem(Random rand, Collection<T> items, double totalWeight)
    {
        if (items.size() == 0)
            throw new IndexOutOfBoundsException();

        double random = rand.nextDouble() * totalWeight;
        T last = null;
        for (T t : items)
        {
            last = t;
            random -= t.getWeight();
            if (random <= 0.0)
                return t;
        }

        return last;
    }

    public static <T> T select(Random rand, Collection<T> items, final Function<T, Double> weightFunction)
    {
        return select(rand, Collections2.transform(items, new Function<T, SimpleItem<T>>()
        {
            @Nullable
            @Override
            public SimpleItem<T> apply(T input)
            {
                return new SimpleItem<>(weightFunction.apply(input), input);
            }
        }));
    }

    public static <T> T select(Random rand, Collection<SimpleItem<T>> items)
    {
        return selectItem(rand, items).getItem();
    }

    public static <T> T select(Random rand, Collection<SimpleItem<T>> items, double totalWeight)
    {
        return selectItem(rand, items, totalWeight).getItem();
    }

    public static interface Item
    {
        double getWeight();
    }

    public static class SimpleItem<T> implements Item
    {
        protected final double weight;
        protected final T item;

        public SimpleItem(double weight, T item)
        {
            this.item = item;
            this.weight = weight;
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
    }
}

