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
import org.objectweb.asm.tree.FieldInsnNode;

/**
 * Created by lukas on 26.04.14.
 */
public class IvNodeMatcherFieldSRG implements IvSingleNodeMatcher
{
    public int opCode;
    public String srgFieldName;
    public String owner;
    public Type type;

    public IvNodeMatcherFieldSRG(int opCode, String srgFieldName, String owner, Type type)
    {
        this.opCode = opCode;
        this.srgFieldName = srgFieldName;
        this.owner = owner;
        this.type = type;
    }

    @Override
    public boolean matchNode(AbstractInsnNode node)
    {
        if (node.getOpcode() != opCode)
        {
            return false;
        }

        FieldInsnNode fieldInsnNode = (FieldInsnNode) node;

        if (srgFieldName != null && !srgFieldName.equals(IvClassTransformer.getSrgName(fieldInsnNode)))
        {
            return false;
        }

        if (owner != null && !owner.equals(IvClassTransformer.getSrgClassName(fieldInsnNode.owner)))
        {
            return false;
        }

        if (type != null && !type.equals(Type.getType(fieldInsnNode.desc)))
        {
            return false;
        }

        return true;
    }
}
