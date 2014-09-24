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

import java.util.ArrayList;

/**
 * Created by lukas on 12.03.14.
 */
public abstract class IvClassTransformerClass extends IvClassTransformer
{
    public ArrayList<String[]> registeredMethods;

    public IvClassTransformerClass(Logger logger)
    {
        super(logger);
        registeredMethods = new ArrayList<String[]>();
    }

    public void registerExpectedMethod(String methodID, String obfName, String signature)
    {
        registeredMethods.add(new String[]{obfName, signature, methodID});
    }

    @Override
    public boolean transform(String className, ClassNode classNode, boolean obf)
    {
        boolean[] sigs = new boolean[registeredMethods.size()];

        for (MethodNode m : classNode.methods)
        {
            for (int methodIndex = 0; methodIndex < registeredMethods.size(); methodIndex++)
            {
                String[] methodInfo = registeredMethods.get(methodIndex);
                String srgName = getSrgName(className, m);
                String srgSignature = getSRGDescriptor(m.desc);

                if ((srgName.equals(methodInfo[0]) && srgSignature.equals(methodInfo[1])))
                {
                    if (transformMethod(className, methodInfo[2], m, obf))
                    {
                        sigs[methodIndex] = true;
                    }
                }
            }
        }

        boolean didChange = false;

        for (int methodIndex = 0; methodIndex < registeredMethods.size(); methodIndex++)
        {
            if (!sigs[methodIndex])
            {
                String[] methodInfo = registeredMethods.get(methodIndex);

                logger.error("Could not transform expected method in class \"" + className + "\" (Obf: " + obf + "): " + methodInfo[0] + " - " + methodInfo[1] + " - " + methodInfo[2]);
            }
            else
            {
                didChange = true;
            }
        }

        return didChange;
    }

    public abstract boolean transformMethod(String className, String methodID, MethodNode methodNode, boolean obf);
}
