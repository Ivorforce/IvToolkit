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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lukas on 12.03.15.
 */
public class Pairs
{
    public static <L, R> Iterable<Pair<L, R>> pairLeft(final L left, Iterable<R> right)
    {
        return Iterables.transform(right, new Function<R, Pair<L, R>>()
        {
            @Nullable
            @Override
            public Pair<L, R> apply(R input)
            {
                return Pair.of(left, input);
            }
        });
    }

    public static <L, R> Iterable<Pair<L, R>> pairRight(Iterable<L> left, final R right)
    {
        return Iterables.transform(left, new Function<L, Pair<L, R>>()
        {
            @Nullable
            @Override
            public Pair<L, R> apply(L input)
            {
                return Pair.of(input, right);
            }
        });
    }

    public static <L, R> Iterable<Pair<L, R>> of(Iterable<L> left, Iterable<R> right)
    {
        return new PairIterable<>(left, right);
    }

    public static <L, R> List<Pair<L, R>> of(List<L> left, List<R> right)
    {
        return new PairList<>(left, right);
    }

    protected static class PairIterable<L, R> implements Iterable<Pair<L, R>>
    {
        protected Iterable<L> left;
        protected Iterable<R> right;

        public PairIterable(Iterable<L> left, Iterable<R> right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public Iterator<Pair<L, R>> iterator()
        {
            return new PairIterator<>(left.iterator(), right.iterator());
        }
    }

    protected static class PairIterator<L, R> implements Iterator<Pair<L, R>>
    {
        protected Iterator<L> left;
        protected Iterator<R> right;

        public PairIterator(Iterator<L> left, Iterator<R> right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean hasNext()
        {
            return left.hasNext() && right.hasNext();
        }

        @Override
        public Pair<L, R> next()
        {
            return Pair.of(left.next(), right.next());
        }

        @Override
        public void remove()
        {
            left.remove();
            right.remove();
        }
    }

    protected static class PairList<L, R> extends AbstractList<Pair<L, R>>
    {
        protected List<L> left;
        protected List<R> right;

        public PairList(List<L> left, List<R> right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public Pair<L, R> get(int index)
        {
            return Pair.of(left.get(index), right.get(index));
        }

        @Override
        public int size()
        {
            return Math.min(left.size(), right.size());
        }

        @Override
        public Iterator<Pair<L, R>> iterator()
        {
            return new PairIterator<>(left.listIterator(), right.listIterator());
        }

        @Override
        public boolean remove(Object o)
        {
            return left.remove(((Pair<L, R>) o).getLeft()) && right.remove(((Pair<L, R>) o).getRight());
        }
    }
}
