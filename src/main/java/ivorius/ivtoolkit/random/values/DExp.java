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
public class DExp implements DValue
{
    public double min;
    public double max;

    public double exp;

    public DExp(double min, double max, double exp)
    {
        this.min = min;
        this.max = max;
        this.exp = exp;
    }

    @Override
    public Double getValue(Random random)
    {
        return min + ((Math.pow(exp, random.nextDouble()) - 1.0) / (exp - 1.0)) * (max - min);
    }
}
