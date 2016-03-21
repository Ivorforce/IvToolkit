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

import java.util.ArrayList;
import java.util.List;

public abstract class IvRaytraceableObject
{
    public Object userInfo;

    private List<IvRaytraceableObject> containedObjects;
    private boolean isContainer;

    public IvRaytraceableObject(Object userInfo)
    {
        this.userInfo = userInfo;

        this.containedObjects = new ArrayList<>();
    }

    /**
     * Adds all the intersections of this object and the specified line to the
     * list. If this object is a container, this method only adds intersections
     * from the contained objects, if this object was hit.
     *
     * @param list The list to add the objects to.
     * @param x    The x-value of the vector origin
     * @param y    The y-value of the vector origin
     * @param z    The z-value of the vector origin
     * @param xDir The x-value of the vector direction
     * @param yDir The x-value of the vector direction
     * @param zDir The x-value of the vector direction
     */
    public void addIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir)
    {
        if (this.isContainer())
        {
            ArrayList<IvRaytracedIntersection> intersections = new ArrayList<>();
            addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);

            if (intersections.size() > 0)
            {
                ArrayList<double[]> hitInnerPoints = new ArrayList<>();

                for (IvRaytraceableObject subObject : this.containedObjects)
                {
                    subObject.addIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
                }
            }
        }
        else
        {
            this.addRaytracedIntersectionsForLineToList(list, x, y, z, xDir, yDir, zDir);
        }
    }

    /**
     * Adds all the intersections of this object and the specified line to the
     * list. This method ignores if this object is a container.
     *
     * @param list The list to add the objects to.
     * @param x    The x-value of the vector origin
     * @param y    The y-value of the vector origin
     * @param z    The z-value of the vector origin
     * @param xDir The x-value of the vector direction
     * @param yDir The x-value of the vector direction
     * @param zDir The x-value of the vector direction
     */
    public abstract void addRaytracedIntersectionsForLineToList(List<IvRaytracedIntersection> list, double x, double y, double z, double xDir, double yDir, double zDir);

    public double[][] getRaytracedIntersectionsForLine(double x, double y, double z, double xDir, double yDir, double zDir)
    {
        ArrayList<IvRaytracedIntersection> intersections = new ArrayList<>();
        addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);

        double[][] intersectionPoints = new double[intersections.size()][];
        for (int i = 0; i < intersections.size(); i++)
        {
            intersectionPoints[i] = intersections.get(i).getPoint();
        }

        return intersectionPoints;
    }

    public IvRaytracedIntersection getFirstHitPointForLine(double x, double y, double z, double xDir, double yDir, double zDir)
    {
        ArrayList<IvRaytracedIntersection> intersections = new ArrayList<>();
        addRaytracedIntersectionsForLineToList(intersections, x, y, z, xDir, yDir, zDir);

        return IvRaytracer.findFirstIntersection(intersections, x, y, z, xDir, yDir, zDir);
    }

    public boolean isContainer()
    {
        return isContainer;
    }

    public void setIsContainer(boolean isContainer)
    {
        this.isContainer = isContainer;
    }

    public void addRaytracedObjectToContainer(IvRaytraceableObject object)
    {
        if (!this.containedObjects.contains(object))
        {
            this.containedObjects.add(object);
        }
    }

    public void removeRaytracedObjectFromContainer(IvRaytraceableObject object)
    {
        this.containedObjects.remove(object);
    }

    public void clearRaytracedObjectsFromContainer()
    {
        this.containedObjects.clear();
    }

    public void drawOutlines()
    {
        this.containedObjects.forEach(IvRaytraceableObject::drawOutlines);
    }
}
