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
