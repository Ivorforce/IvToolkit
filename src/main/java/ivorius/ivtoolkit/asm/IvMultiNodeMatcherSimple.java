/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lukas on 26.04.14.
 */
public class IvMultiNodeMatcherSimple implements IvMultiNodeMatcher
{
    public List<IvSingleNodeMatcher> singleNodeMatchers;

    public IvMultiNodeMatcherSimple(List<IvSingleNodeMatcher> singleNodeMatchers)
    {
        this.singleNodeMatchers = singleNodeMatchers;
    }

    public IvMultiNodeMatcherSimple(IvSingleNodeMatcher... singleNodeMatchers)
    {
        this(Arrays.asList(singleNodeMatchers));
    }

    @Override
    public boolean matchFromNodeInList(InsnList nodes, AbstractInsnNode node)
    {
        for (IvSingleNodeMatcher singleNodeMatcher : singleNodeMatchers)
        {
            if (!singleNodeMatcher.matchNode(node))
            {
                return false;
            }

            node = node.getNext();
        }

        return true;
    }
}
