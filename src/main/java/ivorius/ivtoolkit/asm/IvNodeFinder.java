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

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 26.04.14.
 */
public class IvNodeFinder
{
    public static AbstractInsnNode findNode(IvSingleNodeMatcher matcher, MethodNode methodNode)
    {
        return findNode(matcher, methodNode.instructions);
    }

    public static AbstractInsnNode findNode(IvSingleNodeMatcher matcher, InsnList insnList)
    {
        for (int i = 0; i < insnList.size(); i++)
        {
            AbstractInsnNode node = insnList.get(i);

            if (matcher.matchNode(node))
            {
                return node;
            }
        }

        return null;
    }

    public static AbstractInsnNode findNodeList(IvMultiNodeMatcher matcher, MethodNode methodNode)
    {
        return findNodeList(matcher, methodNode.instructions);
    }

    public static AbstractInsnNode findNodeList(IvMultiNodeMatcher matcher, InsnList insnList)
    {
        for (int i = 0; i < insnList.size(); i++)
        {
            AbstractInsnNode node = insnList.get(i);

            if (matcher.matchFromNodeInList(insnList, node))
            {
                return node;
            }
        }

        return null;
    }

    public static List<AbstractInsnNode> findNodes(IvSingleNodeMatcher matcher, MethodNode methodNode)
    {
        return findNodes(matcher, methodNode.instructions);
    }

    public static List<AbstractInsnNode> findNodes(IvSingleNodeMatcher matcher, InsnList insnList)
    {
        List<AbstractInsnNode> nodes = new ArrayList<AbstractInsnNode>();

        for (int i = 0; i < insnList.size(); i++)
        {
            AbstractInsnNode node = insnList.get(i);

            if (matcher.matchNode(node))
            {
                nodes.add(node);
            }
        }

        return nodes;
    }

    public static List<AbstractInsnNode> findNodeLists(IvMultiNodeMatcher matcher, MethodNode methodNode)
    {
        return findNodeLists(matcher, methodNode.instructions);
    }

    public static List<AbstractInsnNode> findNodeLists(IvMultiNodeMatcher matcher, InsnList insnList)
    {
        List<AbstractInsnNode> nodes = new ArrayList<AbstractInsnNode>();

        for (int i = 0; i < insnList.size(); i++)
        {
            AbstractInsnNode node = insnList.get(i);

            if (matcher.matchFromNodeInList(insnList, node))
            {
                nodes.add(node);
            }
        }

        return nodes;
    }
}
