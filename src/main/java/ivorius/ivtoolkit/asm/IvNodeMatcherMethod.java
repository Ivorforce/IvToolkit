/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
