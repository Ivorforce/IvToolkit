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
