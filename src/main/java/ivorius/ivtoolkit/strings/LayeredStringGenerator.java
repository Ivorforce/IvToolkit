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

package ivorius.ivtoolkit.strings;

import ivorius.ivtoolkit.random.WeightedSelector;
import net.minecraft.util.WeightedRandom;

import java.util.*;

/**
 * Created by lukas on 14.10.14.
 */
public class LayeredStringGenerator
{
    public Layer baseLayer;

    public LayeredStringGenerator(Layer baseLayer)
    {
        this.baseLayer = baseLayer;
    }

    public String randomString(Random random)
    {
        return baseLayer.randomString(random);
    }

    public static interface Layer
    {
        String randomString(Random random);
    }

    public static class LayerSimple implements Layer
    {
        private Map<Character, Layer> subLayers;
        private List<WeightedSelector.SimpleItem<String>> baseStrings = new ArrayList<>();

        public LayerSimple(Map<Character, Layer> subLayers)
        {
            this.subLayers = subLayers;
        }

        public LayerSimple(Object... layersForCharacters)
        {
            this.subLayers = new HashMap<>();

            for (int i = 0; i < layersForCharacters.length / 2; i++)
            {
                this.subLayers.put((Character) layersForCharacters[i * 2], (Layer) layersForCharacters[i * 2 + 1]);
            }
        }

        @Deprecated
        public void addStrings(int weight, String... strings)
        {
            for (String s : strings)
                baseStrings.add(new WeightedSelector.SimpleItem<>(weight, s));
        }

        public void addStrings(double weight, String... strings)
        {
            for (String s : strings)
                baseStrings.add(new WeightedSelector.SimpleItem<>(weight, s));
        }

        @Override
        public String randomString(Random random)
        {
            StringBuilder stringBuilder = new StringBuilder();

            String component = WeightedSelector.select(random, baseStrings);
            for (char aChar : component.toCharArray())
            {
                Layer subLayer = subLayers.get(aChar);

                if (subLayer == null)
                    stringBuilder.append(aChar);
                else
                    stringBuilder.append(subLayer.randomString(random));
            }

            return stringBuilder.toString();
        }
    }

    public static class LayerStatic implements Layer
    {
        private List<WeightedSelector.SimpleItem<String>> baseStrings = new ArrayList<>();

        public LayerStatic(String... baseStrings)
        {
            addStrings(1, baseStrings);
        }

        @Deprecated
        public void addStrings(int weight, String... strings)
        {
            for (String s : strings)
                baseStrings.add(new WeightedSelector.SimpleItem<String>(weight, s));
        }

        public void addStrings(double weight, String... strings)
        {
            for (String s : strings)
                baseStrings.add(new WeightedSelector.SimpleItem<String>(weight, s));
        }

        @Override
        public String randomString(Random random)
        {
            return WeightedSelector.select(random, baseStrings);
        }
    }

    public static class LayerUppercase implements Layer
    {
        public Layer parent;

        public LayerUppercase(Layer parent)
        {
            this.parent = parent;
        }

        @Override
        public String randomString(Random random)
        {
            return firstCharUppercase(parent.randomString(random));
        }

        private static String firstCharUppercase(String name)
        {
            return Character.toString(name.charAt(0)).toUpperCase() + name.substring(1);
        }
    }

    private static <O> O getRandomElementFrom(List<O> list, Random random)
    {
        return list.get(random.nextInt(list.size()));
    }
}
