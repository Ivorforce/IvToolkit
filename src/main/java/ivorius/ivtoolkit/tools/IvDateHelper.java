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

import java.util.Calendar;

import static java.util.Calendar.*;

/**
 * Created by lukas on 01.05.14.
 */
public class IvDateHelper
{
    public static boolean isHalloween()
    {
        Calendar cal = getInstance();

        return cal.get(MONTH) == OCTOBER && cal.get(DAY_OF_MONTH) == 31;
    }

    public static boolean isChristmas()
    {
        Calendar cal = getInstance();

        int day = cal.get(DAY_OF_MONTH);
        return cal.get(MONTH) == DECEMBER && day == 23 || day == 24;
    }

    public static boolean isAprilFools()
    {
        Calendar cal = getInstance();

        return cal.get(MONTH) == APRIL && cal.get(DAY_OF_MONTH) == 1;
    }

    public static boolean isMardiGras()
    {
        Calendar cal = getInstance();

        return cal.get(MONTH) == MARCH && cal.get(DAY_OF_MONTH) == 4;
    }

    public static boolean isOppositeDay()
    {
        Calendar cal = getInstance();

        return cal.get(MONTH) == JANUARY && cal.get(DAY_OF_MONTH) == 25;
    }
}
