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

package ivorius.ivtoolkit.tools;

import net.minecraft.util.text.translation.I18n;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lukas on 17.02.15.
 */
public class IvTranslations
{
    public static boolean has(String key)
    {
        return I18n.canTranslate(key);
    }

    public static String get(String key)
    {
        return I18n.translateToLocal(key);
    }

    public static String format(String key, Object... args)
    {
        return I18n.translateToLocalFormatted(key, args);
    }

    public static List<String> getLines(String key)
    {
        return splitLines(I18n.translateToLocal(key));
    }

    public static List<String> formatLines(String key, Object... args)
    {
        return splitLines(I18n.translateToLocalFormatted(key, args));
    }


    public static List<String> splitLines(String text)
    {
        return Arrays.asList(text.split("<br>"));
    }
}
