/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering.textures;

import ivorius.yegamolchattels.client.rendering.TileEntityRendererStatue;
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

//        TileEntityRendererStatue.saveCachedTexture(modified, resourceLocation.toString());
    }
}
