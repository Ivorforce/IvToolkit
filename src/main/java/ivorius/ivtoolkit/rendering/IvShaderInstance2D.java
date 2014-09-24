/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.rendering;

import org.apache.logging.log4j.Logger;

/**
 * Created by lukas on 18.02.14.
 */
public abstract class IvShaderInstance2D extends IvShaderInstance implements Iv2DScreenEffect
{
    public IvShaderInstance2D(Logger logger)
    {
        super(logger);
    }

    @Override
    public boolean shouldApply(float ticks)
    {
        return getShaderID() > 0;
    }

    @Override
    public abstract void apply(int screenWidth, int screenHeight, float ticks, IvOpenGLTexturePingPong pingPong);

    public void drawFullScreen(int screenWidth, int screenHeight, IvOpenGLTexturePingPong pingPong)
    {
        pingPong.pingPong();
        IvRenderHelper.drawRectFullScreen(screenWidth, screenHeight);
    }

    @Override
    public void destruct()
    {
        deleteShader();
    }
}
