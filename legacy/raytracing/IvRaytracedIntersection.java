/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ivorius.ivtoolkit.raytracing;

public class IvRaytracedIntersection
{
    private IvRaytraceableObject hitObject;
    private Object hitInfo;
    private double[] point;

    public IvRaytracedIntersection(IvRaytraceableObject hitObject, Object hitInfo, double[] point)
    {
        this.hitObject = hitObject;
        this.hitInfo = hitInfo;
        this.point = point;
    }

    public Object getUserInfo()
    {
        return this.hitObject.userInfo;
    }

    public IvRaytraceableObject getHitObject()
    {
        return hitObject;
    }

    public Object getHitInfo()
    {
        return hitInfo;
    }

    public double getX()
    {
        return point[0];
    }

    public double getY()
    {
        return point[1];
    }

    public double getZ()
    {
        return point[2];
    }

    public double[] getPoint()
    {
        return point.clone();
    }

    @Override
    public String toString()
    {
        return String.format("%s: [%.3f, %.3f, %.3f]", String.valueOf(this.getUserInfo()), this.getX(), this.getY(), this.getZ());
    }
}
