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

import ivorius.ivtoolkit.models.Animation;
import ivorius.ivtoolkit.models.NodeAnimation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 23.09.14.
 */
public class Animator
{
    private Animation animation;

    private List<NodeAnimator> nodeAnimators = new ArrayList<>();

    private boolean loop;

    public Animator(Animation animation, boolean loop)
    {
        this.animation = animation;

        for (NodeAnimation nodeAnimation : animation.nodeAnimations)
            nodeAnimators.add(new NodeAnimator(nodeAnimation));

        this.loop = loop;
    }

    public void update(float time, float alpha)
    {
        if (loop)
            time %= animation.duration;

        for (NodeAnimator nodeAnimator : nodeAnimators)
            nodeAnimator.update(time, alpha);
    }

    public void endAnimation()
    {
        update(0.0f, 0.0f);
    }
}
