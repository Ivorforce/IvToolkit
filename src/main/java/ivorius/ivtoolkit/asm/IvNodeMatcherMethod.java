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

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Created by lukas on 26.04.14.
 */
public class IvNodeMatcherMethod implements IvSingleNodeMatcher
{
    public int opCode;
    public String srgMethodName;
    public String owner;
    public String desc;

    public IvNodeMatcherMethod(int opCode, String srgMethodName, String owner, String desc)
    {
        this.opCode = opCode;
        this.srgMethodName = srgMethodName;
        this.owner = owner;
        this.desc = desc;
    }

    public IvNodeMatcherMethod(int opCode, String srgMethodName, String owner, Type returnValue, Type... desc)
    {
        this(opCode, srgMethodName, owner, IvClassTransformer.getMethodDescriptor(returnValue, (Object[])desc));
    }

    @Override
    public boolean matchNode(AbstractInsnNode node)
    {
        if (node.getOpcode() != opCode)
        {
            return false;
        }

        MethodInsnNode methodInsnNode = (MethodInsnNode) node;

        if (srgMethodName != null && !srgMethodName.equals(IvClassTransformer.getSrgName(methodInsnNode)))
        {
            return false;
        }

        if (owner != null && !owner.equals(methodInsnNode.owner))
        {
            return false;
        }

        if (desc != null && !desc.equals(methodInsnNode.desc))
        {
            return false;
        }

        return true;
    }
}
