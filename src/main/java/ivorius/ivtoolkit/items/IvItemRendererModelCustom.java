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
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

/**
 * Created by lukas on 12.11.14.
 */
public class IvItemRendererModelCustom implements IItemRenderer
{
    public ItemModelRenderer model;
    public ResourceLocation texture;
    public float modelSize;
    public float[] translation;
    public float[] rotation;

    public IvItemRendererModelCustom(ItemModelRenderer model, ResourceLocation texture, float modelSize, float[] translation, float[] rotation)
    {
        this.model = model;
        this.texture = texture;
        this.modelSize = modelSize;
        this.translation = translation;
        this.rotation = rotation;
    }

    @Override
    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();

        if (type == IItemRenderer.ItemRenderType.ENTITY)
            GL11.glTranslated(0.0, 1.0, 0.0);
        else if (type == IItemRenderer.ItemRenderType.INVENTORY)
            GL11.glTranslated(0.0, 0.3, 0.0);
        else
            GL11.glTranslated(0.5, 1.0, 0.5);

        GL11.glTranslatef(translation[0], translation[1] + 1.0f, translation[2]);

        if (type != IItemRenderer.ItemRenderType.ENTITY)
        {
            float modelScale = 1.0f / modelSize;
            GL11.glScalef(modelScale, modelScale, modelScale);
        }

        GL11.glRotatef(rotation[0], 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(rotation[1], 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(rotation[2], 0.0f, 0.0f, 1.0f);

        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        model.render(item);

        GL11.glPopMatrix();
    }

    public static interface ItemModelRenderer
    {
        void render(ItemStack stack);
    }

    public static class ItemModelRendererSimple implements ItemModelRenderer
    {
        /**
         * The model to be rendered.
         */
        public IModelCustom model;

        /**
         * The list of render parts for reference. If null, the model will render all parts, otherwise {@link #additive} will be used.
         */
        public String[] renderParts;

        /**
         * Indicates if the {@link #renderParts} are additive (true) or subtractive (false).
         */
        public boolean additive;

        public ItemModelRendererSimple(IModelCustom model)
        {
            this.model = model;
        }

        public ItemModelRendererSimple(IModelCustom model, String[] renderParts, boolean additive)
        {
            this.model = model;
            this.renderParts = renderParts;
            this.additive = additive;
        }

        @Override
        public void render(ItemStack stack)
        {
            if (renderParts != null)
            {
                if (additive)
                    model.renderOnly(renderParts);
                else
                    model.renderAllExcept(renderParts);
            }
            else
                model.renderAll();
        }
    }
}
