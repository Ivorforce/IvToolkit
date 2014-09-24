/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering;

/**
 * Created by lukas on 21.02.14.
 */
public interface Iv2DScreenEffect
{
    public abstract boolean shouldApply(float ticks);

    public abstract void apply(int screenWidth, int screenHeight, float ticks, IvOpenGLTexturePingPong pingPong);

    public abstract void destruct();
}
