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

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTexturePatternColorizer implements IvTextureCreatorMC.LoadingImageEffect
{
    private ResourceLocation resourceLocation;
    private BufferedImage bufferedImage;

    private Logger logger;

    private int[] patternColorComponents = new int[4];
    private float[] patternColor = new float[4];
    private float[] hsb = new float[4];

    public IvTexturePatternColorizer(ResourceLocation resourceLocation, Logger logger)
    {
        this.resourceLocation = resourceLocation;
        this.logger = logger;
    }

    public IvTexturePatternColorizer(BufferedImage bufferedImage, Logger logger)
    {
        this.bufferedImage = bufferedImage;
        this.logger = logger;
    }

    @Override
    public void load(IResourceManager resourceManager) throws IOException
    {
        if (resourceLocation != null)
            bufferedImage = IvTextureCreatorMC.getImage(resourceManager, resourceLocation, logger);
    }

    @Override
    public void getPixel(float[] color, float[] colorDest, int x, int y)
    {
        bufferedImage.getRaster().getPixel(x % bufferedImage.getWidth(), y % bufferedImage.getHeight(), patternColorComponents);

        hsb = Color.RGBtoHSB(patternColorComponents[0], patternColorComponents[1], patternColorComponents[2], null);
        hsb[2] = (0.2126f * color[0] / 255.0f + 0.7152f * color[1] / 255.0f + 0.0722f * color[2] / 255.0f) * 0.3f + hsb[2] * 0.7f;
        patternColor = IvColorHelper.getARBGFloats(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));

        colorDest[0] = color[0] * 0.1f + patternColor[1] * 255.0f * 0.9f;
        colorDest[1] = color[1] * 0.1f + patternColor[2] * 255.0f * 0.9f;
        colorDest[2] = color[2] * 0.1f + patternColor[3] * 255.0f * 0.9f;
        colorDest[3] = color[3] * (patternColorComponents[3] / 255.0f);
    }
}
