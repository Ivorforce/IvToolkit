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

import org.lwjgl.util.vector.Vector3f;

/**
 * Created by lukas on 23.09.14.
 */
public class NodeFieldAnimatorVector3f extends NodeFieldAnimator<Vector3f>
{
    public NodeFieldAnimatorVector3f()
    {
        super(new Vector3f());
    }

    @Override
    public void interpolate(Vector3f dest, Vector3f left, Vector3f right, float progress)
    {
        float rev = 1.0f - progress;
        dest.set(left.x * rev + right.x * progress,
                left.y * rev + right.y * progress,
                left.z * rev + right.z * progress);
    }
}
