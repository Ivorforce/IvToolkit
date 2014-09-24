/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.logic;

/**
 * Created by lukas on 02.08.14.
 */
public class IvObjects
{
    public static boolean equals(Object a, Object b)
    {
        return a == b || (a == null ? b.equals(a) : a.equals(b));
    }
}
