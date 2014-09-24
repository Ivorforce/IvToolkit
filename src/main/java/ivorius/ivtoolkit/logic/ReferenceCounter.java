/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */

package ivorius.ivtoolkit.logic;

import java.util.*;

/**
 * Created by lukas on 28.07.14.
 */
public class ReferenceCounter<V>
{
    private Map<V, Integer> map;
    private Set<V> freeObjects;

    public ReferenceCounter()
    {
        this.map = new HashMap<>();
        freeObjects = new HashSet<>();
    }

    public int getRetainCount(V object)
    {
        Integer val = map.get(object);
        return val != null ? val : 0;
    }

    public boolean hasObject(V object)
    {
        return getRetainCount(object) > 0;
    }

    public void retain(V object, int retainCount)
    {
        int newCount = getRetainCount(object) + retainCount;
        map.put(object, newCount);
    }

    public boolean release(V object, int releaseCount)
    {
        int newCount = getRetainCount(object) - releaseCount;

        if (newCount < 0)
            throw new RuntimeException("Trying to release a freed object!");

        if (newCount == 0)
        {
            freeObjects.add(object);
            map.remove(object);
        }
        else
        {
            map.put(object, newCount);
        }

        return newCount > 0;
    }

    public Set<V> deallocateAllFreeObjects()
    {
        if (freeObjects.size() == 0)
            return Collections.emptySet();

        Set<V> freeObjects = this.freeObjects;
        this.freeObjects = new HashSet<>();
        return freeObjects;
    }

    public int numberOfRetainedObjects()
    {
        return map.size();
    }

    public int numberOfFreeObjects()
    {
        return freeObjects.size();
    }

    public Set<V> getFreeObjects()
    {
        return Collections.unmodifiableSet(freeObjects);
    }

    public Set<V> getAllRetainedObjects()
    {
        return map.keySet();
    }
}
