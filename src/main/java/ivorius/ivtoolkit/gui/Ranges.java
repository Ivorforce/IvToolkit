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

package ivorius.ivtoolkit.gui;

import net.minecraft.util.math.MathHelper;

/**
 * Created by lukas on 19.11.16.
 */
public class Ranges
{
    public static IntegerRange roundedIntRange(FloatRange floatRange)
    {
        return new IntegerRange(MathHelper.floor(floatRange.getMin() + 0.5f), MathHelper.floor(floatRange.getMax() + 0.5f));
    }
}
