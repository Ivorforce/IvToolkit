/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
