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

package ivorius.ivtoolkit.logic;

import gnu.trove.impl.Constants;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by lukas on 28.07.14.
 */
public class ReferenceCounter<V>
{
    private TObjectIntMap<V> map;
    private Set<V> freeObjects;

    public ReferenceCounter()
    {
        map = new TObjectIntHashMap<>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, 0);
        freeObjects = new HashSet<>();
    }

    public int getRetainCount(V object)
    {
        return map.get(object);
    }

    public boolean hasObject(V object)
    {
        return map.containsKey(object);
    }

    public void retain(V object, int retainCount)
    {
        if (retainCount < 1)
            throw new RuntimeException("Attempting to retain an object by a negative amount - use release instead.");

        map.adjustOrPutValue(object, retainCount, retainCount);
    }

    public boolean release(V object, int releaseCount)
    {
        if (releaseCount < 1)
            throw new RuntimeException("Attempting to release an object by a negative amount - use retain instead.");

        int newCount = map.adjustOrPutValue(object, -releaseCount, -releaseCount);

        if (newCount < 0)
            throw new RuntimeException("Attempting to release a freed object!");
        else if (newCount == 0)
        {
            freeObjects.add(object);
            map.remove(object);
        }
        else
            map.put(object, newCount);

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
