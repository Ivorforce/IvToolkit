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
public class RandomValues
{
    public static int[] getValueRange(IValue value, Random random)
    {
        int[] result = new int[2];

        int val1 = value.getValue(random);
        int val2 = value.getValue(random);

        boolean val1F = val1 < val2;
        result[0] = val1F ? val1 : val2;
        result[1] = val1F ? val2 : val1;

        return result;
    }

    public static double[] getValueRange(DValue value, Random random)
    {
        double[] result = new double[2];

        double val1 = value.getValue(random);
        double val2 = value.getValue(random);

        boolean val1F = val1 < val2;
        result[0] = val1F ? val1 : val2;
        result[1] = val1F ? val2 : val1;

        return result;
    }
}
