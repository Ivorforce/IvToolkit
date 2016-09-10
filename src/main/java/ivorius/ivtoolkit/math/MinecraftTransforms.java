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

package ivorius.ivtoolkit.math;

import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by lukas on 30.08.16.
 */
public class MinecraftTransforms
{
    public static AxisAlignedTransform2D from(Rotation rotation, Mirror mirror)
    {
        return AxisAlignedTransform2D.from(from(rotation) + (mirror == Mirror.LEFT_RIGHT ? 2 : 0), mirror != Mirror.NONE);
    }

    public static Pair<Rotation, Mirror> to(AxisAlignedTransform2D transform)
    {
        return Pair.of(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
    }

    public static void useAs(AxisAlignedTransform2D transform, BiConsumer<Rotation, Mirror> consumer)
    {
        consumer.accept(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
    }

    public static <T> T map(AxisAlignedTransform2D transform, BiFunction<Rotation, Mirror, T> consumer)
    {
        return consumer.apply(to(transform.getRotation()), transform.isMirrorX() ? Mirror.FRONT_BACK : Mirror.NONE);
    }

    public static Rotation to(int rotation)
    {
        switch (rotation)
        {
            case 0:
                return Rotation.NONE;
            case 1:
                return Rotation.CLOCKWISE_90;
            case 2:
                return Rotation.CLOCKWISE_180;
            case 3:
            default:
                return Rotation.COUNTERCLOCKWISE_90;
        }
    }

    public static int from(Rotation rotation)
    {
        switch (rotation)
        {
            case NONE:
                return 0;
            case CLOCKWISE_90:
                return 1;
            case CLOCKWISE_180:
                return 2;
            case COUNTERCLOCKWISE_90:
            default:
                return 3;
        }
    }
}
