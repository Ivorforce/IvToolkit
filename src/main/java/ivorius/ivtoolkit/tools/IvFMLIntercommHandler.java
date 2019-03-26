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

package ivorius.ivtoolkit.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Created by lukas on 07.06.14.
 */
public abstract class IvFMLIntercommHandler
{
    private Logger logger;
    private String modOwnerID;
    private String modID;

    protected IvFMLIntercommHandler(Logger logger, String modOwnerID, String modID)
    {
        this.logger = logger;
        this.modOwnerID = modOwnerID;
        this.modID = modID;
    }

    public Logger getLogger()
    {
        return logger;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    public String getModOwnerID()
    {
        return modOwnerID;
    }

    public void setModOwnerID(String modOwnerID)
    {
        this.modOwnerID = modOwnerID;
    }

    public String getModID()
    {
        return modID;
    }

    public void setModID(String modID)
    {
        this.modID = modID;
    }

    public void handleMessages(boolean server)
    {
        InterModComms.getMessages(modID).forEach(message -> {
            onIMCMessage(message, server, true);
        });
    }

    public void onIMCMessage(InterModComms.IMCMessage message, boolean server, boolean runtime)
    {
        try {
            boolean didHandle = handleMessage(message, server, runtime);

            if (!didHandle) {
                logger.warn("Could not handle message with key '" + message.getMethod() + "'");
            }
        }
        catch (Exception ex) {
            logger.error("Exception on message with key '" + message.getMethod() + "'");
            ex.printStackTrace();
        }
    }

    protected abstract boolean handleMessage(InterModComms.IMCMessage message, boolean server, boolean runtime);

    @Nonnull
    protected <T> Optional<T> messageAs(String key, InterModComms.IMCMessage message, Class<T> expectedType)
    {
        if (key.equals(message.getMethod())) {
            Object thing = message.getMessageSupplier().get();

            if (thing.getClass().isAssignableFrom(expectedType)) {
                return Optional.of((T) thing);
            }

            faultyMessage(message, expectedType);
        }

        return Optional.empty();
    }

    protected Entity getEntity(NBTTagCompound compound, boolean server)
    {
        return getEntity(compound, "worldID", "entityID", server);
    }

    protected Entity getEntity(NBTTagCompound compound, String worldKey, String entityKey, boolean server)
    {
        if (!server) {
            return Minecraft.getInstance().world.getEntityByID(compound.getInt(entityKey));
        }
        else {
            DimensionType worldID = DimensionType.getById(compound.getInt(worldKey));

            return ServerLifecycleHooks.getCurrentServer().getWorld(worldID).getEntityByID(compound.getInt(entityKey));
        }
    }

    protected <T> boolean sendReply(IvIntercommMessages.Replyable<T> message, T value)
    {
        if (message.consumer == null) {
            return false;
        }

        message.consumer.accept(value);

        return true;
    }

    private void faultyMessage(InterModComms.IMCMessage message, Class expectedType)
    {
        logger.error("Got message with key '" + message.getMethod() + "' of type '" + message.getMessageSupplier().get() + "'; Expected type: '" + expectedType.getName() + "'");
    }
}

