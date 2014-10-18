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

package ivorius.ivtoolkit.models.attributes;

import ivorius.ivtoolkit.models.Attribute;

public class FloatAttribute extends Attribute
{
	public static final String ShininessAlias = "shininess";
	public static final long Shininess = register(ShininessAlias);

	public static FloatAttribute createShininess (float value) {
		return new FloatAttribute(Shininess, value);
	}

	public static final String AlphaTestAlias = "alphaTest";
	public static final long AlphaTest = register(AlphaTestAlias);

	public static FloatAttribute createAlphaTest (float value) {
		return new FloatAttribute(AlphaTest, value);
	}

	public float value;

	public FloatAttribute (long type) {
		super(type);
	}

	public FloatAttribute (long type, float value) {
		super(type);
		this.value = value;
	}

	@Override
	public Attribute copy () {
		return new FloatAttribute(type, value);
	}

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FloatAttribute that = (FloatAttribute) o;

        if (Float.compare(that.value, value) != 0) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        return result;
    }
}
