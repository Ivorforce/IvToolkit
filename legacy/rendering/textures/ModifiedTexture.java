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

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by lukas on 26.07.14.
 */
public class ModifiedTexture extends AbstractTexture
{
    private ResourceLocation resourceLocation;
    private IvTextureCreatorMC.LoadingImageEffect imageEffect;
    private Logger logger;

    public ModifiedTexture(ResourceLocation resourceLocation, IvTextureCreatorMC.LoadingImageEffect imageEffect, Logger logger)
    {
        this.resourceLocation = resourceLocation;
        this.imageEffect = imageEffect;
        this.logger = logger;
    }

    @Override
    public void loadTexture(IResourceManager var1) throws IOException
    {
        imageEffect.load(var1);
        BufferedImage image = IvTextureCreatorMC.getImage(var1, resourceLocation, logger);

        if (image == null)
            throw new IOException();

        BufferedImage modified = IvTextureCreator.applyEffect(image, imageEffect);

        TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), modified, false, false);
    }
}
