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

package ivorius.ivtoolkit;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import ivorius.ivtoolkit.tools.JavaCompatibility;

import java.util.Map;

/**
 * Created by lukas on 21.02.14.
 */
@IFMLLoadingPlugin.Name(value = "IvToolkit")
@IFMLLoadingPlugin.TransformerExclusions(value = {"ivorius.ivtoolkit.asm."})
// warning! This class is compiled separately from all other classes
// this means: this class cannot reference anything else and nobody else can reference this
// this is because this class is compiled for Java 6 to enable the warning message
public class IvToolkitLoadingPlugin implements IFMLLoadingPlugin
{
    @Override
    public String[] getASMTransformerClass()
    {
        // must do this here, because constructor is too early for FMLCommonHandler
        JavaCompatibility.requireJava8(true);

        return new String[]{};
    }

    @Override
    public String getModContainerClass()
    {
        return "ivorius.ivtoolkit.IvToolkitCoreContainer";
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}