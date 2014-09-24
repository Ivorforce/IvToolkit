/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
 */
package ivorius.ivtoolkit.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ivorius.ivtoolkit.tools.IvSideClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

/**
 * Created by lukas on 02.07.14.
 */
public class PacketExtendedEntityPropertiesDataHandler implements IMessageHandler<PacketExtendedEntityPropertiesData, IMessage>
{
    @Override
    public IMessage onMessage(PacketExtendedEntityPropertiesData message, MessageContext ctx)
    {
        World world = IvSideClient.getClientWorld();
        Entity entity = world.getEntityByID(message.getEntityID());

        if (entity != null)
        {
            IExtendedEntityProperties eep = entity.getExtendedProperties(message.getEepKey());

            if (eep != null)
                ((PartialUpdateHandler) eep).readUpdateData(message.getPayload(), message.getContext());
        }

        return null;
    }
}
