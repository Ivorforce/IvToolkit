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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

/**
 * Created by lukas on 26.04.14.
 */
public class IvNodeMatcherLDC implements IvSingleNodeMatcher
{
    public String cst;

    public IvNodeMatcherLDC(String cst)
    {
        this.cst = cst;
    }

    @Override
    public boolean matchNode(AbstractInsnNode node)
    {
        if (node.getOpcode() != Opcodes.LDC)
        {
            return false;
        }

        LdcInsnNode ldcInsnNode = (LdcInsnNode) node;

        if (cst != null && !cst.equals(ldcInsnNode.cst))
        {
            return false;
        }

        return true;
    }
}
