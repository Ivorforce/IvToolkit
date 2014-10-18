/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.ivtoolkit.models.textures;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 27.09.14.
 */
public class MinecraftTextureProvider implements TextureProvider
{
    private String basePath;

    public MinecraftTextureProvider(String basePath)
    {
        this.basePath = basePath;
    }

    @Override
    public Texture provideTexture(String textureName)
    {
        return new Texture(new ResourceLocation(basePath + textureName));
    }

    public static class Texture implements ivorius.ivtoolkit.models.textures.Texture
    {
        private ResourceLocation resourceLocation;

        public Texture(ResourceLocation resourceLocation)
        {
            this.resourceLocation = resourceLocation;
        }

        @Override
        public void bindTexture()
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        }

        @Override
        public float minU()
        {
            return 0;
        }

        @Override
        public float maxU()
        {
            return 1;
        }

        @Override
        public float minV()
        {
            return 0;
        }

        @Override
        public float maxV()
        {
            return 1;
        }
    }
}
