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

package ivorius.ivtoolkit.rendering;

import ivorius.ivtoolkit.tools.IvDependencyLoaderMC;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 14.01.15.
 */
public class IvShaderLoaderMC extends IvDependencyLoaderMC
{
    public IvShaderLoaderMC()
    {
        super("<ivimport:", ">");
    }

    public void setUpShader(IvShaderInstance shaderInstance, ResourceLocation vertexShader, ResourceLocation fragmentShader) throws LoadException, RecursionException
    {
        String vShader = loadResource(vertexShader.toString());
        String fShader = loadResource(fragmentShader.toString());
        shaderInstance.trySettingUpShader(vShader, fShader);
    }
}
