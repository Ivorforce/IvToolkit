/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
