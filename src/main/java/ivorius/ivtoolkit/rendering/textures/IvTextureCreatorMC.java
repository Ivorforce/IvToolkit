/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering.textures;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by lukas on 26.07.14.
 */
public class IvTextureCreatorMC
{
    public static BufferedImage getImage(IResourceManager resourceManager, ResourceLocation location, Logger logger) throws IOException
    {
        ITextureObject textureObject = Minecraft.getMinecraft().getTextureManager().getTexture(location);

        if (textureObject instanceof ThreadDownloadImageData)
        {
            return ReflectionHelper.getPrivateValue(ThreadDownloadImageData.class, (ThreadDownloadImageData) textureObject, "bufferedImage", "field_110560_d");
        }
        else if (textureObject instanceof LayeredTexture)
        {
            BufferedImage bufferedimage = null;

            for (Object layeredTextureNameObj : ((LayeredTexture) textureObject).layeredTextureNames)
            {
                String layeredTextureName = (String) layeredTextureNameObj;

                if (layeredTextureName != null)
                {
                    try (InputStream inputstream = resourceManager.getResource(new ResourceLocation(layeredTextureName)).getInputStream())
                    {
                        BufferedImage bufferImageLayer = ImageIO.read(inputstream);

                        if (bufferedimage == null)
                        {
                            bufferedimage = new BufferedImage(bufferImageLayer.getWidth(), bufferImageLayer.getHeight(), 2);
                        }

                        bufferedimage.getGraphics().drawImage(bufferImageLayer, 0, 0, null);
                    }
                }
            }

            return bufferedimage;
        }
        else if (textureObject instanceof PreBufferedTexture)
        {
            return ((PreBufferedTexture) textureObject).getBufferedImage();
        }
        else
        {
            IResource iresource = resourceManager.getResource(location);

            try (InputStream inputstream = iresource.getInputStream())
            {
                return ImageIO.read(inputstream);
            }
        }
    }

    public static interface LoadingImageEffect extends IvTextureCreator.ImageEffect
    {
        void load(IResourceManager resourceManager) throws IOException;
    }
}
