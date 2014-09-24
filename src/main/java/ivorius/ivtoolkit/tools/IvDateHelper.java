/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
