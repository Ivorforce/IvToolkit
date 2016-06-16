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

package ivorius.ivtoolkit.random.values;

import java.util.Random;

/**
 * Created by lukas on 04.04.14.
 */
public class IFlags implements IValue
{
    public int[] flags;
    public double[] chances;
    public int minFlags;

    public IFlags(int minFlags, int[] flags, double[] chances)
    {
        this.minFlags = minFlags;
        this.flags = flags;
        this.chances = chances;
    }

    public IFlags(int minFlags, Object... flagsWithChances)
    {
        this.minFlags = minFlags;
        this.flags = new int[flagsWithChances.length / 2];
        this.chances = new double[flags.length];

        for (int i = 0; i < flags.length; i++)
        {
            flags[i] = (Integer) flagsWithChances[i * 2];
            chances[i] = (Double) flagsWithChances[i * 2 + 1];
        }
    }

    @Override
    public Integer getValue(Random random)
    {
        int value = 0;
        int flagsSet = 0;

        for (int i = 0; i < flags.length; i++)
        {
            if (random.nextDouble() < chances[i])
            {
                value |= 1 << flags[i];
                flagsSet++;
            }
        }

        while (flagsSet < minFlags)
        {
            int[] values = new int[flags.length - flagsSet];
            double[] weights = new double[values.length];

            int currentIndex = 0;
            for (int i = 0; i < values.length; i++)
            {
                while ((flagsSet & (1 << flags[currentIndex])) > 0)
                {
                    currentIndex++;
                }

                values[i] = flags[currentIndex];
                weights[i] = chances[currentIndex];
                currentIndex++;
            }

            value |= 1 << getRandomValue(random, values, weights);
            flagsSet++;
        }

        return value;
    }

    public static int getRandomValue(Random random, int[] values, double[] weights)
    {
        double total = 0.0;
        for (double weight : weights)
        {
            total += weight;
        }

        double selected = random.nextDouble() * total;

        for (int i = 0; i < weights.length; i++)
        {
            selected -= weights[i];
            if (selected < 0.0)
            {
                return values[i];
            }
        }

        throw new RuntimeException("Weights have invalid values!");
    }
}
