/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.logic;

public class IvChatLine
{
    public int delay;
    public String lineString;

    public IvChatLine(int par1, String par2Str)
    {
        this.lineString = par2Str;
        this.delay = par1;
    }
}
