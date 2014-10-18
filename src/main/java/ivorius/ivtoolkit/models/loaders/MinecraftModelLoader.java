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

package ivorius.ivtoolkit.models.loaders;

import ivorius.ivtoolkit.models.Model;
import ivorius.ivtoolkit.models.textures.TextureProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by lukas on 21.09.14.
 */
public class MinecraftModelLoader
{
    private static Logger logger = LogManager.getLogger("Models");
    private static G3DModelLoader g3DModelLoader = new G3DModelLoader(logger);

    public static Model loadModelG3DJ(ResourceLocation resourceLocation, TextureProvider textureProvider)
    {
        return g3DModelLoader.createModel(new InputStreamReader(streamFromResourceLocation(resourceLocation)),
                textureProvider);
    }

    public static InputStream streamFromResourceLocation(ResourceLocation resourceLocation)
    {
        return MinecraftModelLoader.class.getResourceAsStream("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
    }
}
