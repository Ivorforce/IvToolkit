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

import java.util.List;

public class IvRaytraceableBox extends IvRaytraceableObject
{
    private double x, y, z;

    private double width, height, depth;

    private IvRaytraceableAxisAlignedSurface[] surfaces;

    public IvRaytraceableBox(Object userInfo, double x, double y, double z, double width, double height, double depth)
    {
        super(userInfo);

        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;

        this.surfaces = new IvRaytraceableAxisAlignedSurface[6];
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }

    public double getDepth()
    {
        return depth;
    }

    @Override
    public void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir)
    {
        for (IvRaytraceableAxisAlignedSurface surface : this.surfaces)
        {
            surface.addRaytracedIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
        }
    }
}
