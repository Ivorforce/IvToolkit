/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;

public class IvParticleHelper
{
    public static void spawnParticle(EntityFX particle)
    {
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }
}
