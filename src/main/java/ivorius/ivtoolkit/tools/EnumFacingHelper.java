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

import net.minecraft.util.EnumFacing;

/**
 * Created by lukas on 22.07.15.
 */
public class EnumFacingHelper
{
    public static EnumFacing byName(String name)
    {
        return byName(name, null);
    }

    public static EnumFacing byName(String name, EnumFacing defaultFacing)
    {
        for (EnumFacing facing : EnumFacing.values())
        {
            if (facing.getName().equals(name))
                return facing;
        }

        return defaultFacing;
    }
}
