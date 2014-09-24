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

package ivorius.ivtoolkit.tools;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lukas on 12.06.14.
 */
public class IvGsonHelper
{
    public static String serializedName(Enum anEnum)
    {
        String name = anEnum.name();

        try
        {
            SerializedName serializedName = anEnum.getClass().getField(name).getAnnotation(SerializedName.class);

            if (serializedName != null)
            {
                name = serializedName.value();
            }
        }
        catch (NoSuchFieldException ignored)
        {

        }

        return name;
    }

    public static <E extends Enum> E enumForName(String serializedName, E[] values)
    {
        for (E anEnum : values)
        {
            if (serializedName(anEnum).equals(serializedName))
                return anEnum;
        }

        return null;
    }
}
