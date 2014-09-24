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

package ivorius.ivtoolkit.rendering;

import org.apache.logging.log4j.Logger;

/**
 * Created by lukas on 18.02.14.
 */
public abstract class IvShaderInstance3D extends IvShaderInstance
{
    public IvShaderInstance3D(Logger logger)
    {
        super(logger);
    }

    public abstract boolean activate(float partialTicks, float ticks);

    public abstract void deactivate();
}
