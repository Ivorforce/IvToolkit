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

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntCollection;
import ivorius.ivtoolkit.gui.IntegerRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by lukas on 19.09.16.
 */
public class LineSelection
{
    @Nonnull
    private final IntArrayList cuts = new IntArrayList();
    private boolean startsAdditive;

    public LineSelection(boolean additive)
    {
        this.startsAdditive = additive;
    }

    public LineSelection(boolean startsAdditive, IntCollection cuts)
    {
        this.startsAdditive = startsAdditive;
        this.cuts.addAll(cuts);
        this.cuts.sort(Integer::compareTo);
    }

    public static LineSelection fromRange(IntegerRange range, boolean additive)
    {
        return new LineSelection(!additive, new IntArrayList(new int[]{range.getMin(), range.getMax() + 1}));
    }

    public LineSelection copy()
    {
        return new LineSelection(startsAdditive, cuts);
    }

    public void invert()
    {
        startsAdditive = !startsAdditive;
    }

    public LineSelection inverted()
    {
        LineSelection copy = copy();
        copy.invert();
        return copy;
    }

    public void set(LineSelection selection)
    {
        cuts.clear();
        cuts.addAll(selection.cuts);
        startsAdditive = selection.startsAdditive;
    }

    public void set(LineSelection selection, boolean additive, boolean setAdditive)
    {
        selection.streamSections(null, additive).forEach(range -> setSection(range, setAdditive));
    }

    public void setSection(@Nullable IntegerRange range, boolean additive)
    {
        if (range == null) {
            cuts.clear();
            startsAdditive = additive;
            return;
        }

        int min = range.min;
        int max = range.max;

        int minSection = sectionForIndex(min);
        int maxSection = sectionForIndex(max);

        Boolean minAdditive = min == Integer.MIN_VALUE ? null : isSectionAdditive(sectionForIndex(min - 1));
        Boolean maxAdditive = max == Integer.MAX_VALUE ? null : isSectionAdditive(sectionForIndex(max + 1));

        // Remove cuts in between
        cuts.removeElements(minSection, maxSection);

        // Adjust line between before and section
        if (minAdditive != null)
            ensureLeftCut(min, minAdditive != additive);
        else
            startsAdditive = additive; // There is nothing left left, set it to what we need

        // Adjust line between section and after
        if (maxAdditive != null)
            ensureLeftCut(max + 1, additive != maxAdditive);
    }

    private void ensureLeftCut(int index, boolean needsCut)
    {
        int leftCutIndex = sectionForIndex(index) - 1;

        if (needsCut) {
            if (!hasCutLeft(leftCutIndex, index))
                cuts.add(leftCutIndex + 1, index);
        }
        else {
            if (hasCutLeft(leftCutIndex, index))
                cuts.removeElements(leftCutIndex, leftCutIndex + 1);
        }
    }

    private boolean hasCutLeft(int cutIndex, int index)
    {
        return cutIndex >= 0 && cuts.getInt(cutIndex) == index;
    }

    public IntStream streamElements(@Nullable IntegerRange range, boolean additive)
    {
        return streamSections(range, additive).flatMapToInt(r ->
                IntStream.range(r.min, r.max + 1));
    }

    public Stream<IntegerRange> streamSections(@Nullable IntegerRange range, boolean additive)
    {
        int minSection = range == null ? 0 : sectionForIndex(range.min);
        int maxSection = range == null ? sectionCount() - 1 : sectionForIndex(range.max);

        if (isSectionAdditive(minSection) != additive)
            minSection++;

        int finalMinSection = minSection;
        return IntStream.range(0, (maxSection - minSection + 2) / 2)
                .mapToObj(i -> sectionRange(finalMinSection + i * 2));
    }

    public IntegerRange sectionRange(int section)
    {
        assertHasSection(section);
        return new IntegerRange(section > 0 ? cuts.getInt(section - 1) : Integer.MIN_VALUE, section < cuts.size() ? cuts.getInt(section) - 1 : Integer.MAX_VALUE);
    }

    public int sectionCount()
    {
        return cuts.size() + 1;
    }

    public boolean isUniform()
    {
        return cuts.isEmpty();
    }

    public boolean isUniform(boolean additive)
    {
        return cuts.isEmpty() && startsAdditive == additive;
    }

    public int sectionForIndex(int index)
    {
        if (index == Integer.MIN_VALUE)
            return 0;
        if (index == Integer.MAX_VALUE)
            return sectionCount() - 1;

        int res = IntArrays.binarySearch(cuts.elements(), index);
        // If we find the index, it is in the section after the cut
        // If we don't find it, the value is -(index + 1), so add +1 for both
        return Math.abs(res + 1);
    }

    public boolean isSectionAdditive(int section)
    {
        assertHasSection(section);

        return ((startsAdditive ? section : section + 1) % 2) == 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineSelection that = (LineSelection) o;

        if (startsAdditive != that.startsAdditive) return false;
        return cuts.equals(that.cuts);

    }

    @Override
    public int hashCode()
    {
        int result = cuts.hashCode();
        result = 31 * result + (startsAdditive ? 1 : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "LineSelection{" +
                "cuts=" + cuts +
                ", startsAdditive=" + startsAdditive +
                '}';
    }

    private void assertHasSection(int section)
    {
        if (section < 0 || section >= sectionCount())
            throw new ArrayIndexOutOfBoundsException(String.format("section: %d, size: %d", section, sectionCount()));
    }
}
