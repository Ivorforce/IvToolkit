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

import java.util.AbstractList;
import java.util.List;

/**
 * Created by lukas on 12.03.15.
 */
public class Ranges
{
    public static List<Integer> rangeList(int start, int end, int stride)
    {
        return new RangeList(start, end, end < start ? -stride : stride);
    }

    public static int[] range(int start, int end, int stride)
    {
        if (end < start)
        {
            int[] range = new int[(start - end) / stride];
            for (int i = 0; i < range.length; i++)
                range[i] = i * -stride + start;
            return range;
        }
        else
        {
            int[] range = new int[(end - start) / stride];
            for (int i = 0; i < range.length; i++)
                range[i] = i * stride + start;
            return range;
        }
    }

    public static List<Integer> rangeList(int start, int end)
    {
        return rangeList(start, end, 1);
    }

    public static int[] range(int start, int end)
    {
        return range(start, end, 1);
    }

    public static List<Integer> toIterable(int end)
    {
        return rangeList(0, end, 1);
    }

    public static int[] to(int end)
    {
        return range(0, end, 1);
    }

    protected static class RangeList extends AbstractList<Integer>
    {
        protected int start;
        protected int end;
        protected int stride;

        public RangeList(int start, int end, int stride)
        {
            this.start = start;
            this.end = end;
            this.stride = stride;
        }

        @Override
        public Integer get(int index)
        {
            if (index >= size() || index < 0)
                throw new IndexOutOfBoundsException();

            return start + stride * index;
        }

        @Override
        public int size()
        {
            if (stride > 0)
                return (end - start + (stride - 1)) / stride;
            else
                return (end - start + (stride + 1)) / stride;
        }
    }
}
