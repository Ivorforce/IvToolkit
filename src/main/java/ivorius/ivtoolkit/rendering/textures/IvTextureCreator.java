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

package ivorius.ivtoolkit.rendering.textures;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTextureCreator
{
    public static BufferedImage applyEffect(BufferedImage texture, ImageEffect effect)
    {
        if (texture.getType() == BufferedImage.TYPE_BYTE_INDEXED) // INDEXED does not work... TODO?
        {
            BufferedImage notIndexed = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
            notIndexed.getGraphics().drawImage(texture, 0, 0, null);
            texture = notIndexed;
        }

        BufferedImage modified = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
        WritableRaster sourceRaster = texture.getRaster();
        WritableRaster destRaster = modified.getRaster();

        float[] normalizedPixelColors = new float[4];
        float[] normalizedPixelColorsDest = new float[4];

        for (int x = 0; x < texture.getWidth(); x++)
            for (int y = 0; y < texture.getHeight(); y++)
            {
                sourceRaster.getPixel(x, y, normalizedPixelColors);
                effect.getPixel(normalizedPixelColors, normalizedPixelColorsDest, x, y);
                destRaster.setPixel(x, y, normalizedPixelColorsDest);
            }

        return modified;
    }

    public static interface ImageEffect
    {
        void getPixel(float[] color, float[] colorDest, int x, int y);
    }
}
