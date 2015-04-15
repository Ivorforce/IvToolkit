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

import java.util.*;

/**
 * Created by lukas on 15.04.15.
 */
public class ShiftedMazeComponent<M extends MazeComponent<C>, C> implements MazeComponent<C>
{
    private final M component;
    private final MazeRoom shift;

    private final Set<MazeRoom> rooms = new HashSet<>();
    private final Map<MazeRoomConnection, C> exits = new HashMap<>();

    public ShiftedMazeComponent(M component, MazeRoom shift)
    {
        this.component = component;
        this.shift = shift;
    }

    public ShiftedMazeComponent(M component, MazeRoom shift, Set<MazeRoom> rooms, Map<MazeRoomConnection, C> exits)
    {
        this(component, shift);
        this.rooms.addAll(rooms);
        this.exits.putAll(exits);
    }

    public M getComponent()
    {
        return component;
    }

    public MazeRoom getShift()
    {
        return shift;
    }

    @Override
    public Set<MazeRoom> rooms()
    {
        return Collections.unmodifiableSet(rooms);
    }

    @Override
    public Map<MazeRoomConnection, C> exits()
    {
        return Collections.unmodifiableMap(exits);
    }
}
