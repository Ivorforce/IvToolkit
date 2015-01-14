/*
 * Copyright 2015 Lukas Tenbrink
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

package ivorius.ivtoolkit.tools;

import com.google.common.base.Charsets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

/**
 * Created by lukas on 14.01.15.
 */
public class IvDependencyLoaderMC extends IvDependencyLoader
{
    public IvDependencyLoaderMC(String importStart, String importEnd)
    {
        super(importStart, importEnd);
    }

    @Override
    public String loadResourceRaw(String location) throws LoadException
    {
        try
        {
            IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
            IResource vShaderRes = resourceManager.getResource(new ResourceLocation(location));
            return IOUtils.toString(vShaderRes.getInputStream(), Charsets.UTF_8);
        }
        catch (Throwable e)
        {
            throw new LoadException(e);
        }
    }
}
