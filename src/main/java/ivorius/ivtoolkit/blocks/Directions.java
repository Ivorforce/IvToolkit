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

package ivorius.ivtoolkit.blocks;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.tools.IvGsonHelper;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nullable;

import static net.minecraft.util.EnumFacing.*;

/**
 * Created by lukas on 03.03.15.
 */
public class Directions
{
    public static final EnumFacing[] HORIZONTAL = new EnumFacing[]{NORTH, EAST, SOUTH, WEST};
    public static final EnumFacing[] X_AXIS = new EnumFacing[]{EAST, WEST};
    public static final EnumFacing[] Y_AXIS = new EnumFacing[]{UP, DOWN};
    public static final EnumFacing[] Z_AXIS = new EnumFacing[]{SOUTH, NORTH};
    
    @Nullable
    public static Integer getHorizontalClockwiseRotations(EnumFacing source, EnumFacing dest, boolean mirrorX)
    {
        if (source == dest)
            return mirrorX && ArrayUtils.contains(Directions.X_AXIS, dest) ? 2 : 0;

        int arrayIndexSrc = ArrayUtils.indexOf(HORIZONTAL, source);
        int arrayIndexDst = ArrayUtils.indexOf(HORIZONTAL, dest);

        if (arrayIndexSrc >= 0 && arrayIndexDst >= 0)
        {
            int mirrorRotations = mirrorX && ArrayUtils.contains(Directions.X_AXIS, dest) ? 2 : 0;
            return ((arrayIndexDst - arrayIndexSrc + mirrorRotations) + HORIZONTAL.length) % HORIZONTAL.length;
        }

        return null;
    }

    public static EnumFacing rotate(EnumFacing direction, AxisAlignedTransform2D transform)
    {
        if (direction == UP || direction == DOWN)
            return direction;

        int rotations = transform.getRotation();
        if (transform.isMirrorX() && ArrayUtils.contains(X_AXIS, direction))
            rotations += 2;
        return HORIZONTAL[(ArrayUtils.indexOf(HORIZONTAL, direction) + rotations) % HORIZONTAL.length];
    }

    public static EnumFacing deserialize(String id)
    {
        EnumFacing direction = IvGsonHelper.enumForNameIgnoreCase(id, values());
        return direction != null ? direction : NORTH;
    }

    public static EnumFacing deserializeHorizontal(String id)
    {
        EnumFacing direction = IvGsonHelper.enumForNameIgnoreCase(id, HORIZONTAL);
        return direction != null ? direction : NORTH;
    }

    public static String serialize(EnumFacing direction)
    {
        return IvGsonHelper.serializedName(direction);
    }

    public static EnumFacing getDirectionFromVRotation(int front)
    {
        switch (front)
        {
            default:
            case 0:
                return SOUTH;
            case 1:
                return EAST;
            case 2:
                return NORTH;
            case 3:
                return WEST;
        }
    }
}
