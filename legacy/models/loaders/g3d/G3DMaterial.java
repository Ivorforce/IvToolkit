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

package ivorius.ivtoolkit.models.loaders.g3d;

import java.awt.*;

public class G3DMaterial
{
	public String id;

	public String type;

	public Color ambient;
	public Color diffuse;
	public Color specular;
	public Color emissive;
	public Color reflection;

	public float shininess;
	public float opacity = 1.f;

	public G3DTexture[] textures;
}
