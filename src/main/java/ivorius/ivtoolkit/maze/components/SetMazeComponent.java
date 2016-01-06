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

package ivorius.ivtoolkit.maze.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by lukas on 15.04.15.
 */
public class SetMazeComponent<C> implements MorphingMazeComponent<C>
{
    public final Set<MazeRoom> rooms = new HashSet<>();
    public final Map<MazeRoomConnection, C> exits = new HashMap<>();

    public SetMazeComponent()
    {
    }

    public SetMazeComponent(Set<MazeRoom> rooms, Map<MazeRoomConnection, C> exits)
    {
        this.rooms.addAll(rooms);
        this.exits.putAll(exits);
    }

    @Override
    public Set<MazeRoom> rooms()
    {
        return rooms;
    }

    @Override
    public Map<MazeRoomConnection, C> exits()
    {
        return exits;
    }

    @Override
    public void add(MazeComponent<C> component)
    {
        rooms.addAll(component.rooms());

        // Remove all solved connections, and add the ones still open from the other component
        for (Map.Entry<MazeRoomConnection, C> entry : component.exits().entrySet())
            if (exits.remove(entry.getKey()) == null)
                exits.put(entry.getKey(), entry.getValue());
    }

    @Override
    public void set(MazeComponent<C> component)
    {
        rooms.clear();
        rooms.addAll(component.rooms());

        exits.clear();
        exits.putAll(component.exits());
    }

    @Override
    public MorphingMazeComponent<C> copy()
    {
        return new SetMazeComponent<>(rooms, exits);
    }
}
