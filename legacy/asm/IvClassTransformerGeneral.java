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

package ivorius.ivtoolkit.asm;

import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Created by lukas on 12.03.14.
 */
public abstract class IvClassTransformerGeneral extends IvClassTransformer
{
    protected IvClassTransformerGeneral(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean transform(String className, ClassNode classNode, boolean obf)
    {
        boolean didChange = false;

        for (MethodNode m : classNode.methods)
        {
            if (transformMethod(className, m, obf))
            {
                didChange = true;
            }
        }

        return didChange;
    }

    public abstract boolean transformMethod(String className, MethodNode methodNode, boolean obf);
}
