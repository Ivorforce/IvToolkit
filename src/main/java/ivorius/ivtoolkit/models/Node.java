/*
 * Notice: This is a modified version of a libgdx file. See https://github.com/libgdx/libgdx for the original work.
 *
 * Copyright 2011 See libgdx AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ivorius.ivtoolkit.models;

import ivorius.ivtoolkit.models.utils.MatrixMathUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 21.09.14.
 */
public class Node
{
    /**
     * the id, may be null, FIXME is this unique? *
     */
    public String id;
    /**
     * parent node, may be null *
     */
    public Node parent;
    /**
     * child nodes *
     */
    public final List<Node> children = new ArrayList<>(2);
    /**
     * Whether this node is currently being animated, if so the translation, rotation and scale values are not used.
     */
    public boolean isAnimated;
    /**
     * the translation, relative to the parent, not modified by animations *
     */
    public final Vector3f translation = new Vector3f();
    /**
     * the rotation, relative to the parent, not modified by animations *
     */
    public final Quaternion rotation = new Quaternion(0, 0, 0, 1);
    /**
     * the scale, relative to the parent, not modified by animations *
     */
    public final Vector3f scale = new Vector3f(1, 1, 1);
    /**
     * the local transform, based on translation/rotation/scale calculateLocalTransform or any applied animation *
     */
    public final Matrix4f localTransform = new Matrix4f();
    /**
     * the global transform, product of local transform and transform of the parent node, calculated via
     * calculateWorldTransform *
     */
    public final Matrix4f globalTransform = new Matrix4f();

    public List<NodePart> parts = new ArrayList<>(2);

    public void calculateLocalTransform()
    {
        MatrixMathUtils.setTRS(localTransform, translation, rotation, scale);
    }

    public void calculateGlobalTransform()
    {
        globalTransform.load(localTransform);
        if (parent != null)
            Matrix4f.mul(parent.globalTransform, globalTransform, globalTransform);
    }

    /**
     * Calculates the local and world transform of this node and optionally all its children.
     *
     * @param recursive whether to calculate the local/world transforms for children.
     */
    public void calculateTransforms(boolean recursive)
    {
        calculateLocalTransform();
        calculateGlobalTransform();

        if (recursive)
        {
            for (Node child : children)
                child.calculateTransforms(true);
        }
    }

    public void calculateBoneTransforms(boolean recursive)
    {
        for (final NodePart part : parts)
        {
            if (part.invBoneBindTransforms == null || part.bones == null || part.invBoneBindTransforms.size != part.bones.length)
                continue;
            final int n = part.invBoneBindTransforms.size;
            for (int i = 0; i < n; i++)
            {
                part.bones[i].load(part.invBoneBindTransforms.keys[i].globalTransform);
                Matrix4f.mul(part.bones[i], part.invBoneBindTransforms.values[i], part.bones[i]);
            }
        }
        if (recursive)
        {
            for (Node child : children)
                child.calculateBoneTransforms(true);
        }
    }
}
