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

import java.util.Random;

public class IvStringHelper
{
    public static String cheeseString(String string, float effect, long seed)
    {
        return cheeseString(string, effect, new Random(seed));
    }

    public static String cheeseString(String string, float effect, Random rand)
    {
        if (effect <= 0.0f)
        {
            return string;
        }

        StringBuilder builder = new StringBuilder(string.length());

        for (int i = 0; i < string.length(); i++)
        {
            if (rand.nextFloat() <= effect)
            {
                builder.append(' ');
            }
            else
            {
                builder.append(string.charAt(i));
            }
        }

        return builder.toString();
    }

    public static int countOccurrences(String haystack, char needle)
    {
        int count = 0;

        for (int i = 0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }

        return count;
    }
}
