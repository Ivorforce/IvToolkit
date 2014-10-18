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

package ivorius.ivtoolkit.models.animation;

import ivorius.ivtoolkit.models.Node;
import ivorius.ivtoolkit.models.NodeAnimation;
import ivorius.ivtoolkit.models.NodeKeyframe;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by lukas on 23.09.14.
 */
public class NodeAnimator
{
    private NodeAnimation nodeAnimation;

    private NodeFieldAnimator<Vector3f> animatorTranslation;
    private NodeFieldAnimator<Vector3f> animatorScale;
    private NodeFieldAnimator<Quaternion> animatorRotation;

    public NodeAnimator(NodeAnimation nodeAnimation)
    {
        this.nodeAnimation = nodeAnimation;

        animatorTranslation = new NodeFieldAnimatorVector3f();
        animatorScale = new NodeFieldAnimatorVector3f();
        animatorRotation = new NodeFieldAnimatorQuaternion();

        for (NodeKeyframe nodeKeyframe : nodeAnimation.keyframes)
        {
            if (nodeKeyframe.translation != null)
                animatorTranslation.addKeyframe(nodeKeyframe.translation, nodeKeyframe.keytime);
            if (nodeKeyframe.scale != null)
                animatorScale.addKeyframe(nodeKeyframe.scale, nodeKeyframe.keytime);
            if (nodeKeyframe.rotation != null)
                animatorRotation.addKeyframe(nodeKeyframe.rotation, nodeKeyframe.keytime);
        }

        // No partial node animations allowed
        if (animatorTranslation.keyframeCount() == 0)
            animatorTranslation.addKeyframe(nodeAnimation.node.translation, 0.0f);
        if (animatorScale.keyframeCount() == 0)
            animatorScale.addKeyframe(nodeAnimation.node.scale, 0.0f);
        if (animatorRotation.keyframeCount() == 0)
            animatorRotation.addKeyframe(nodeAnimation.node.rotation, 0.0f);
    }

    public void update(float time, float alpha)
    {
        Node node = nodeAnimation.node;

        animatorTranslation.update(node.translation, time, alpha);
        animatorScale.update(node.scale, time, alpha);
        animatorRotation.update(node.rotation, time, alpha);
    }
}
