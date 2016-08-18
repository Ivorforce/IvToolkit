/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

/**
 * Created by lukas on 18.08.16.
 */
public class CapabilityUpdateRegistry
{
    public static final CapabilityUpdateRegistry INSTANCE = new CapabilityUpdateRegistry();

    private final BiMap<String, Capability> capabilityBiMap = HashBiMap.create();

    public Capability register(@Nullable String key, @Nullable Capability value)
    {
        return capabilityBiMap.put(key, value);
    }

    public Capability capability(String key)
    {
        return capabilityBiMap.get(key);
    }

    public String key(Capability key)
    {
        return capabilityBiMap.inverse().get(key);
    }
}
