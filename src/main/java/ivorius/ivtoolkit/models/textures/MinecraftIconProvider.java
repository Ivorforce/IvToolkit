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

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Created by lukas on 27.09.14.
 */
public class MinecraftIconProvider implements TextureProvider
{
    private ResourceLocation texture;
    private Map<String, IIcon> specialIconMap;
    private IIcon defaultIcon;

    public MinecraftIconProvider(ResourceLocation texture, Map<String, IIcon> specialIconMap, IIcon defaultIcon)
    {
        this.texture = texture;
        this.specialIconMap = specialIconMap;
        this.defaultIcon = defaultIcon;
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    public void setTexture(ResourceLocation texture)
    {
        this.texture = texture;
    }

    public Map<String, IIcon> getSpecialIconMap()
    {
        return specialIconMap;
    }

    public void setSpecialIconMap(Map<String, IIcon> specialIconMap)
    {
        this.specialIconMap = specialIconMap;
    }

    @Override
    public Texture provideTexture(String textureName)
    {
        IIcon icon;

        IIcon specialIcon = specialIconMap.get(textureName);
        icon = specialIcon != null ? specialIcon : defaultIcon;

        Texture tex = new MinecraftTextureProvider.Texture(texture);
        return new TextureSub(tex, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
    }
}
