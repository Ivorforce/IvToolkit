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

package ivorius.ivtoolkit.blocks;

import java.util.Iterator;

/**
 * Created by lukas on 09.06.14.
 */
public class BlockAreaIterator implements Iterator<BlockCoord>
{
    private BlockCoord lower;
    private BlockCoord higher;

    private int curX;
    private int curY;
    private int curZ;

    public BlockAreaIterator(BlockCoord lower, BlockCoord higher)
    {
        this.lower = lower;
        this.higher = higher;

        curX = lower.x;
        curY = lower.y;
        curZ = lower.z;
    }

    public BlockAreaIterator(BlockArea area)
    {
        this(area.getLowerCorner(), area.getHigherCorner());
    }

    @Override
    public boolean hasNext()
    {
        return curX <= higher.x && curY <= higher.y && curZ <= higher.z;
    }

    @Override
    public BlockCoord next()
    {
        BlockCoord retVal = hasNext() ? new BlockCoord(curX, curY, curZ) : null;

        curX ++;

        if (curX > higher.x)
        {
            curX = lower.x;
            curY ++;

            if (curY > higher.y)
            {
                curY = lower.y;
                curZ ++;
            }
        }

        return retVal;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException();
    }
}
