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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 23.09.14.
 */
public abstract class NodeFieldAnimator<F>
{
    private List<Keyframe> keyframes = new ArrayList<>();
    private F temp;

    public NodeFieldAnimator(F temp)
    {
        this.temp = temp;
    }

    public void addKeyframe(F object, float keyframe)
    {
        keyframes.add(new Keyframe(object, keyframe));
    }

    public int keyframeCount()
    {
        return keyframes.size();
    }

    public void update(F field, float time, float alpha)
    {
        InterpolatedKeyframe interpolatedKeyframe = interpolatedKeyframe(time);

        interpolate(temp, interpolatedKeyframe.left, interpolatedKeyframe.right, interpolatedKeyframe.time);
        interpolate(field, field, temp, alpha);
    }

    public abstract void interpolate(F dest, F left, F right, float progress);

    private InterpolatedKeyframe interpolatedKeyframe(float time)
    {
        int rightIndex = rightKeyframeIndex(time);
        Keyframe rightKeyframe = keyframes.get(rightIndex);

        if (rightIndex == 0)
            return new InterpolatedKeyframe(rightKeyframe.object);

        Keyframe leftKeyframe = keyframes.get(rightIndex - 1);
        return new InterpolatedKeyframe(leftKeyframe.object, rightKeyframe.object,
                (time - leftKeyframe.time) / (rightKeyframe.time - leftKeyframe.time));
    }

    private int rightKeyframeIndex(float time)
    {
        for (int i = 0; i < keyframes.size(); i++)
        {
            if (keyframes.get(i).time > time)
                return i;
        }

        return keyframes.size() - 1;
    }

    protected class Keyframe
    {
        public F object;
        public float time;

        public Keyframe(F object, float time)
        {
            this.object = object;
            this.time = time;
        }
    }

    private class InterpolatedKeyframe
    {
        public F left;
        public F right;
        public float time;

        public InterpolatedKeyframe(F left)
        {
            this.left = left;
            this.right = left;
            this.time = 0.0f;
        }

        public InterpolatedKeyframe(F left, F right, float time)
        {
            this.left = left;
            this.right = right;
            this.time = time;
        }
    }
}