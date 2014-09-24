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

package ivorius.ivtoolkit.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 18.04.14.
 */
public class IvItemRendererModel implements IItemRenderer
{
    public ItemModelRenderer model;
    public ResourceLocation texture;
    public float modelSize;
    public float[] translation;
    public float[] rotation;

    public IvItemRendererModel(ItemModelRenderer model, ResourceLocation texture, float modelSize, float[] translation, float[] rotation)
    {
        this.model = model;
        this.texture = texture;
        this.modelSize = modelSize;
        this.translation = translation;
        this.rotation = rotation;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();

        if (type == ItemRenderType.ENTITY)
        {
            GL11.glTranslated(0.0, 1.0, 0.0);
        }
        else if (type == ItemRenderType.INVENTORY)
        {
            GL11.glTranslated(0.0, 0.3, 0.0);
        }
        else
        {
            GL11.glTranslated(0.5, 1.0, 0.5);
        }

        GL11.glTranslatef(translation[0], translation[1] + 1.0f, translation[2]);

        if (type != ItemRenderType.ENTITY)
        {
            float modelScale = 1.0f / modelSize;
            GL11.glScalef(modelScale, modelScale, modelScale);
        }

        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        model.render(item);

        GL11.glPopMatrix();
    }

    public static interface ItemModelRenderer
    {
        void render(ItemStack stack);
    }
}
