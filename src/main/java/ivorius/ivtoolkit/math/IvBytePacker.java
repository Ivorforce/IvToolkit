/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.math;

import net.minecraft.util.MathHelper;

/**
 * Created by lukas on 13.07.14.
 */
public class IvBytePacker
{
    public static byte getRequiredBitLength(int maxValue)
    {
        byte bits = 0;
        while (maxValue > 0)
        {
            maxValue >>>= 1;
            bits ++;
        }
        return bits;
    }

    public static byte[] packValues(int[] values, byte bitLength)
    {
        byte[] packed = new byte[MathHelper.ceiling_float_int(values.length * bitLength / 8.0f)];
        int currentArrayIndex = 0;

        long currentVal = 0;
        byte currentSavedBits = 0;

        for (int value : values)
        {
            currentVal = (currentVal << bitLength) | value;
            currentSavedBits += bitLength;

            while (currentSavedBits >= 8)
            {
                packed[currentArrayIndex] = (byte) (currentVal >>> (currentSavedBits - 8));
                currentSavedBits -= 8;

                currentVal = deleteLeftBits(currentVal, 64 - currentSavedBits);
                currentArrayIndex++;
            }
        }

        if (currentSavedBits > 0)
            packed[currentArrayIndex] = (byte) (currentVal << (8 - currentSavedBits));

        return packed;
    }

    public static int[] unpackValues(byte[] packed, byte bitLength, int valueCount)
    {
        int[] values = new int[valueCount];
        int currentArrayIndex = 0;

        long currentVal = 0;
        byte currentSavedBits = 0;

        for (byte value : packed)
        {
            currentVal = (currentVal << 8) | moveMSBToBytePos((long) value);
            currentSavedBits += 8;

            while (currentSavedBits >= bitLength && currentArrayIndex < valueCount)
            {
                values[currentArrayIndex] = (int)(currentVal >>> (currentSavedBits - bitLength));
                currentSavedBits -= bitLength;

                currentVal = deleteLeftBits(currentVal, 64 - currentSavedBits);
                currentArrayIndex ++;
            }
        }

        return values;
    }

    private static long moveMSBToBytePos(long value)
    {
        return ((value >>> (Long.SIZE - 8)) & 128) | (value & 127);
    }

    private static long deleteLeftBits(long val, int bits)
    {
        return bits >= 64 ? 0 : ((val << bits) >>> bits);
    }
}
