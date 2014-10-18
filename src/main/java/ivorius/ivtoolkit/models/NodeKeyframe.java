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

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by lukas on 22.09.14.
 */
public class NodeKeyframe
{
    /** the timestamp of this keyframe **/
    public float keytime;
    /** the translation, given in local space, relative to the parent **/
    public Vector3f translation;
    /** the scale, given in local space relative to the parent **/
    public Vector3f scale;
    /** the rotation, given in local space, relative to the parent **/
    public Quaternion rotation;
}
