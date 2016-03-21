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

package ivorius.ivtoolkit;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by lukas on 25.02.14.
 */
public class IvToolkitCoreContainer extends DummyModContainer
{
    public static Logger logger = LogManager.getLogger(IvToolkit.MODID);

    public IvToolkitCoreContainer()
    {
        super(new ModMetadata());

        ModMetadata myMeta = super.getMetadata();
        myMeta.authorList = Collections.singletonList("Ivorius");
        myMeta.description = "Uncategorized framework";
        myMeta.modId = IvToolkit.MODID;
        myMeta.version = IvToolkit.VERSION;
        myMeta.name = IvToolkit.NAME;
        myMeta.url = "http://www.minecraftforum.net/topic/563257-172";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        bus.register(this);
        return true;
    }
}
