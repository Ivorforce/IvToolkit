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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.*;
import ivorius.ivtoolkit.IvToolkitCoreContainer;
import ivorius.ivtoolkit.random.WeightedSelector;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Created by lukas on 15.04.15.
 */
public class MazeComponentConnector
{
    public static <M extends WeightedMazeComponent<C>, C> List<ShiftedMazeComponent<M, C>> randomlyConnect(MorphingMazeComponent<C> morphingComponent, List<M> components,
                                                                    ConnectionStrategy<C> connectionStrategy, final MazeComponentPlacementStrategy<M, C> placementStrategy, Random random)
    {
        List<ShiftedMazeComponent<M, C>> result = new ArrayList<>();
        Deque<Triple<MazeRoom, MazeRoomConnection, C>> exitStack = new ArrayDeque<>();

        Predicate<ShiftedMazeComponent<M, C>> componentPredicate = Predicates.and(MazeComponents.<M, C>compatibilityPredicate(morphingComponent, connectionStrategy),
                MazeComponentPlacementStrategies.placeable(placementStrategy));
        WeightedSelector.WeightFunction<ShiftedMazeComponent<M, C>> weightFunction = MazeComponentConnector.getWeightFunction();

        addAllExits(placementStrategy, exitStack, morphingComponent.exits().entrySet());

        while (exitStack.size() > 0)
        {
            Triple<MazeRoom, MazeRoomConnection, C> triple = exitStack.removeLast();
            MazeRoom room = triple.getLeft();

            if (morphingComponent.rooms().contains(room))
                continue; // Has been filled while queued

            MazeRoomConnection exit = triple.getMiddle();
            C connection = triple.getRight();

            List<ShiftedMazeComponent<M, C>> placeable = FluentIterable.from(components).transformAndConcat(MazeComponents.<M, C>shiftAllFunction(exit, connection, connectionStrategy)).filter(componentPredicate).toList();

            if (placeable.size() == 0)
            {
                IvToolkitCoreContainer.logger.warn("Did not find fitting component for maze!");
                IvToolkitCoreContainer.logger.warn("Suggested: X with exits " + FluentIterable.from(morphingComponent.exits().entrySet()).filter(entryConnectsTo(room)));
                continue;
            }

            ShiftedMazeComponent<M, C> selected = WeightedSelector.canSelect(placeable, weightFunction)
                ? WeightedSelector.select(random, placeable, weightFunction)
                : placeable.get(random.nextInt(placeable.size()));

            addAllExits(placementStrategy, exitStack, selected.exits().entrySet());

            morphingComponent.add(selected);
            result.add(selected);
        }

        return result;
    }

    private static Predicate<Map.Entry<MazeRoomConnection, ?>> entryConnectsTo(final MazeRoom finalRoom)
    {
        return new Predicate<Map.Entry<MazeRoomConnection, ?>>()
        {
            @Override
            public boolean apply(@Nullable Map.Entry<MazeRoomConnection, ?> input)
            {
                return input != null && (input.getKey().has(finalRoom));
            }
        };
    }

    private static <M extends WeightedMazeComponent<C>, C> void addAllExits(MazeComponentPlacementStrategy<M, C> placementStrategy, Deque<Triple<MazeRoom, MazeRoomConnection, C>> exitStack, Set<Map.Entry<MazeRoomConnection, C>> entries)
    {
        for (Map.Entry<MazeRoomConnection, C> exit : entries)
        {
            if (placementStrategy.shouldContinue(exit.getKey().getLeft()))
                exitStack.add(Triple.of(exit.getKey().getLeft(), exit.getKey(), exit.getValue()));
            if (placementStrategy.shouldContinue(exit.getKey().getRight()))
                exitStack.add(Triple.of(exit.getKey().getRight(), exit.getKey(), exit.getValue()));
        }
    }

    private static <M extends WeightedMazeComponent<C>, C> WeightedSelector.WeightFunction<ShiftedMazeComponent<M, C>> getWeightFunction()
    {
        return new WeightedSelector.WeightFunction<ShiftedMazeComponent<M, C>>()
        {
            @Override
            public double apply(ShiftedMazeComponent<M, C> item)
            {
                return item.getComponent().getWeight();
            }
        };
    }
}
