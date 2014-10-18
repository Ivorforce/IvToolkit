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

import ivorius.ivtoolkit.models.utils.ArrayMap;
import org.lwjgl.util.vector.Matrix4f;

/**
 * Created by lukas on 22.09.14.
 */
public class NodePart
{
    /**
     * The MeshPart (shape) to render. Must not be null.
     */
    public MeshPart meshPart;
    /**
     * The Material used to render the {@link #meshPart}. Must not be null.
     */
    public Material material;
    /**
     * Mapping to each bone (node) and the inverse transform of the bind pose. Will be used to fill the {@link #bones} array. May
     * be null.
     */
    public ArrayMap<Node, Matrix4f> invBoneBindTransforms;
    /**
     * The current transformation (relative to the bind pose) of each bone, may be null. When the part is skinned, this will be
     * updated by a call to {@link Model#calculateTransforms()}. Do not set or change this value manually.
     */
    public Matrix4f[] bones;
    /**
     * true by default. If set to false, this part will not participate in rendering and bounding box calculation.
     */
    public boolean enabled = true;
}
