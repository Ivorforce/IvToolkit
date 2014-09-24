/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.asm;

import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by lukas on 26.04.14.
 */
public class IvInsnHelper
{
    public static void insertDUP3(InsnList list)
    {
        list.add(new InsnNode(DUP2_X1));    // 1 2 3 1 2
        list.add(new InsnNode(POP2));       // 3 1 2
        list.add(new InsnNode(DUP_X2));     // 3 1 2 3
        list.add(new InsnNode(DUP_X2));     // 3 1 2 3 3
        list.add(new InsnNode(POP));        // 1 2 3 3
        list.add(new InsnNode(DUP2_X1));    // 1 2 3 1 2 3
    }

    public static void insertDUP4(InsnList list)
    {
        list.add(new InsnNode(DUP2_X2));    // 1 2 3 4 1 2
        list.add(new InsnNode(POP2));       // 3 4 1 2
        list.add(new InsnNode(DUP2_X2));    // 3 4 1 2 3 4
        list.add(new InsnNode(DUP2_X2));    // 3 4 1 2 3 4 3 4
        list.add(new InsnNode(POP2));       // 1 2 3 4 3 4
        list.add(new InsnNode(DUP2_X2));    // 1 2 3 4 1 2 3 4
    }
}
