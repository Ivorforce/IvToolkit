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

package ivorius.ivtoolkit.models.utils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by lukas on 22.09.14.
 */
public class MatrixMathUtils
{
    public static Matrix4f setTRS(Matrix4f matrix, Vector3f translation, Quaternion quaternion, Vector3f scale)
    {
        quaternion = quaternion.normalise(new Quaternion());
        return setTRS(matrix, translation.getX(), translation.getY(), translation.getZ(), quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getW(), scale.getX(), scale.getY(), scale.getZ());
    }

    public static Matrix4f setTRS(Matrix4f matrix, float translationX, float translationY, float translationZ, float quaternionX, float quaternionY, float quaternionZ, float quaternionW, float scaleX, float scaleY, float scaleZ)
    {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        matrix.m00 = scaleX * (1.0f - (yy + zz));
        matrix.m10 = scaleY * (xy - wz);
        matrix.m20 = scaleZ * (xz + wy);
        matrix.m30 = translationX;

        matrix.m01 = scaleX * (xy + wz);
        matrix.m11 = scaleY * (1.0f - (xx + zz));
        matrix.m21 = scaleZ * (yz - wx);
        matrix.m31 = translationY;

        matrix.m02 = scaleX * (xz - wy);
        matrix.m12 = scaleY * (yz + wx);
        matrix.m22 = scaleZ * (1.0f - (xx + yy));
        matrix.m32 = translationZ;

        matrix.m03 = 0.f;
        matrix.m13 = 0.f;
        matrix.m23 = 0.f;
        matrix.m33 = 1.0f;

        return matrix;
    }

    public static void mul(Matrix4f src, float alpha)
    {
        src.m00 *= alpha;
        src.m01 *= alpha;
        src.m02 *= alpha;
        src.m03 *= alpha;

        src.m10 *= alpha;
        src.m11 *= alpha;
        src.m12 *= alpha;
        src.m13 *= alpha;

        src.m20 *= alpha;
        src.m21 *= alpha;
        src.m22 *= alpha;
        src.m23 *= alpha;

        src.m30 *= alpha;
        src.m31 *= alpha;
        src.m32 *= alpha;
        src.m33 *= alpha;
    }

    public static void add(Matrix4f src, Matrix4f dst, float alpha)
    {
        dst.m00 += src.m00 * alpha;
        dst.m01 += src.m01 * alpha;
        dst.m02 += src.m02 * alpha;
        dst.m03 += src.m03 * alpha;

        dst.m10 += src.m10 * alpha;
        dst.m11 += src.m11 * alpha;
        dst.m12 += src.m12 * alpha;
        dst.m13 += src.m13 * alpha;

        dst.m20 += src.m20 * alpha;
        dst.m21 += src.m21 * alpha;
        dst.m22 += src.m22 * alpha;
        dst.m23 += src.m23 * alpha;

        dst.m30 += src.m30 * alpha;
        dst.m31 += src.m31 * alpha;
        dst.m32 += src.m32 * alpha;
        dst.m33 += src.m33 * alpha;
    }
}
