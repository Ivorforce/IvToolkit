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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lukas on 22.09.14.
 */
public class NodeAnimation implements Comparator<NodeKeyframe>
{
    /**
     * the Node affected by this animation *
     */
    public Node node;
    /**
     * the keyframes, sorted by time, ascending *
     */
    public List<NodeKeyframe> keyframes = new ArrayList<>();

    public void addKeyframe(NodeKeyframe keyframe)
    {
        keyframes.add(keyframe);
        Collections.sort(keyframes, this);
    }

    @Override
    public int compare(NodeKeyframe o1, NodeKeyframe o2)
    {
        return Float.compare(o1.keytime, o2.keytime);
    }
}
