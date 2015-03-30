/*
 * Copyright 2015 Lukas Tenbrink
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

package ivorius.ivtoolkit.rendering.grid;

import ivorius.ivtoolkit.rendering.IvRenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 21.02.15.
 */
public class GridRenderer
{
    public static void renderGrid(int lines, float spacing, float lineLength, float lineWidth)
    {
        Tessellator.instance.startDrawingQuads();

        for (int x = -lines; x <= lines; x++)
            for (int z = -lines; z <= lines; z++)
                renderLine(x * spacing, -lineLength * 0.5f, z * spacing, ForgeDirection.UP, lineLength, lineWidth);

        for (int x = -lines; x <= lines; x++)
            for (int y = -lines; y <= lines; y++)
                renderLine(x * spacing, y * spacing, -lineLength * 0.5f, ForgeDirection.SOUTH, lineLength, lineWidth);

        for (int y = -lines; y <= lines; y++)
            for (int z = -lines; z <= lines; z++)
                renderLine(-lineLength * 0.5f, y * spacing, z * spacing, ForgeDirection.EAST, lineLength, lineWidth);

        Tessellator.instance.draw();
    }

    public static void renderLine(float x, float y, float z, ForgeDirection direction, float length, float size)
    {
        float xDir = direction.offsetX * length;
        float yDir = direction.offsetY * length;
        float zDir = direction.offsetZ * length;

        if (xDir == 0)
            xDir = size;
        else
            x += xDir * 0.5;

        if (yDir == 0)
            yDir = size;
        else
            y += yDir * 0.5;

        if (zDir == 0)
            zDir = size;
        else
            z += zDir * 0.5;

        Tessellator.instance.addTranslation(x, y, z);
        IvRenderHelper.renderCuboid(Tessellator.instance, xDir, yDir, zDir, 1f);
        Tessellator.instance.addTranslation(-x, -y, -z);
    }
}
