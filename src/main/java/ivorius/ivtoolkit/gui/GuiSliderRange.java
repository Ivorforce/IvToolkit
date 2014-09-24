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

package ivorius.ivtoolkit.gui;

/**
 * Created by lukas on 12.06.14.
 */
public class GuiSliderRange extends GuiSliderMultivalue
{
    public GuiSliderRange(int id, int x, int y, int width, int height, String displayString)
    {
        super(id, x, y, width, height, 2, displayString);
    }

    public void setRange(FloatRange range)
    {
        boolean firstLower = getValue(0) < getValue(1);

        setValue(firstLower ? 0 : 1, range.getMin());
        setValue(firstLower ? 1 : 0, range.getMax());
    }

    public FloatRange getRange()
    {
        return new FloatRange(Math.min(getValue(0), getValue(1)), Math.max(getValue(0), getValue(1)));
    }
}
