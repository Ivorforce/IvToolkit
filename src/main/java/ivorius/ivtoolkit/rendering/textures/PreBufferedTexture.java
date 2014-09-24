/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering.textures;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by lukas on 27.07.14.
 */
public class PreBufferedTexture extends AbstractTexture
{
    private BufferedImage bufferedImage;

    public PreBufferedTexture(BufferedImage bufferedImage)
    {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage()
    {
        return bufferedImage;
    }

    @Override
    public void loadTexture(IResourceManager var1) throws IOException
    {
        TextureUtil.uploadTextureImageAllocate(this.getGlTextureId(), bufferedImage, false, false);
    }
}
