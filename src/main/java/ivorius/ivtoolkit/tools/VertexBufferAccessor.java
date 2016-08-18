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

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.entity.EntityCreature;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

/**
 * Created by lukas on 22.07.15.
 */
public class VertexBufferAccessor
{
    protected static Field xOffset;
    protected static Field yOffset;
    protected static Field zOffset;

    protected static Field getXOffset()
    {
        return xOffset != null
                ? xOffset
                : (xOffset = ReflectionHelper.findField(VertexBuffer.class, "xOffset", "field_179004_l"));
    }

    protected static Field getYOffset()
    {
        return yOffset != null
                ? yOffset
                : (yOffset = ReflectionHelper.findField(VertexBuffer.class, "yOffset", "field_179005_m"));
    }

    protected static Field getZOffset()
    {
        return zOffset != null
                ? zOffset
                : (zOffset = ReflectionHelper.findField(VertexBuffer.class, "zOffset", "field_179002_n"));
    }

    public static void addTranslation(VertexBuffer renderer, double x, double y, double z)
    {
        try
        {
            renderer.setTranslation(getXOffset().getDouble(renderer) + x, getYOffset().getDouble(renderer) + y, getZOffset().getDouble(renderer) + z);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}
