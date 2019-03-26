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

package ivorius.ivtoolkit.bezier;

import ivorius.ivtoolkit.math.IvVecMathHelper;

public class IvBezierPoint3DCachedStep
{
    private IvBezierPoint3D leftPoint;
    private int leftPointIndex;
    private double leftPointProgress;

    private IvBezierPoint3D rightPoint;
    private int rightPointIndex;
    private double rightPointProgress;

    private double bezierPathProgress;
    private double innerProgress;

    private double[] cachedPosition;

    public IvBezierPoint3DCachedStep(IvBezierPoint3D leftPoint, int leftPointIndex, IvBezierPoint3D rightPoint, int rightPointIndex, double leftPointProgress, double rightPointProgress, double innerProgress)
    {
        this.leftPoint = leftPoint;
        this.leftPointIndex = leftPointIndex;
        this.rightPoint = rightPoint;
        this.rightPointIndex = rightPointIndex;
        this.leftPointProgress = leftPointProgress;
        this.rightPointProgress = rightPointProgress;
        this.innerProgress = innerProgress;

        this.bezierPathProgress = leftPointProgress + (rightPointProgress - leftPointProgress) * innerProgress;
    }

    public IvBezierPoint3D getLeftPoint()
    {
        return leftPoint;
    }

    public int getLeftPointIndex()
    {
        return leftPointIndex;
    }

    public IvBezierPoint3D getRightPoint()
    {
        return rightPoint;
    }

    public int getRightPointIndex()
    {
        return rightPointIndex;
    }

    public double getLeftPointProgress()
    {
        return leftPointProgress;
    }

    public double getRightPointProgress()
    {
        return rightPointProgress;
    }

    public double getBezierPathProgress()
    {
        return bezierPathProgress;
    }

    public double getInnerProgress()
    {
        return innerProgress;
    }

    public double[] getPosition()
    {
        if (cachedPosition == null)
        {
            double[] bezierFrom = leftPoint.getBezierDirectionPointFrom();
            double[] bezierTo = rightPoint.getBezierDirectionPointTo();

            cachedPosition = IvVecMathHelper.cubicMix(leftPoint.position, bezierFrom, bezierTo, rightPoint.position, getInnerProgress());
        }

        return cachedPosition;
    }
}
