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

package ivorius.ivtoolkit.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerUpper;
import com.mojang.datafixers.Dynamic;
import ivorius.ivtoolkit.IvToolkit;
import ivorius.ivtoolkit.tools.MCRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.fixes.BlockStateFlatteningMap;
import net.minecraft.util.datafix.fixes.BlockStateFlatternEntities;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;

/**
 * Created by lukas on 06.05.16.
 */
public class BlockStates
{
    @Nonnull
    public static IBlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull NBTTagCompound compound)
    {
        if (!compound.contains("Name", 8)) {
            return Blocks.AIR.getDefaultState();
        }
        else {
            Block block = registry.blockFromID(new ResourceLocation(compound.getString("Name")));
            IBlockState iblockstate = block.getDefaultState();

            if (compound.contains("Properties", 10)) {
                NBTTagCompound propertyCompound = compound.getCompound("Properties");
                StateContainer<Block, IBlockState> stateContainer = block.getStateContainer();

                for (String name : propertyCompound.keySet()) {
                    IProperty<?> iproperty = stateContainer.getProperty(name);
                    if (iproperty != null) {
                        iblockstate = setValueHelper(iblockstate, iproperty, name, propertyCompound, compound);
                    }
                }
            }

            return iblockstate;
        }

    }

    private static <S extends IStateHolder<S>, T extends Comparable<T>> S setValueHelper(S val, IProperty<T> property, String name, NBTTagCompound propertyCompound, NBTTagCompound p_193590_4_)
    {
        Optional<T> optional = property.parseValue(propertyCompound.getString(name));
        if (optional.isPresent()) {
            return (S) (val.with(property, (T) (optional.get())));
        }
        else {
            IvToolkit.logger.warn("Unable to read property: {} with value: {} for blockstate: {}", name, propertyCompound.getString(name), p_193590_4_.toString());
            return val;
        }
    }

    @Nonnull
    public static NBTTagCompound writeBlockState(@Nonnull MCRegistry registry, @Nonnull IBlockState state)
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("Name", registry.idFromBlock(state.getBlock()).toString());

        ImmutableMap<IProperty<?>, Comparable<?>> properties = state.getValues();
        if (!properties.isEmpty()) {
            NBTTagCompound propertyCompound = new NBTTagCompound();

            for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
                IProperty<?> property = entry.getKey();

                propertyCompound.setString(
                        property.getName(),
                        ((IProperty) property).getName(entry.getValue())
                );
            }

            nbttagcompound.setTag("Properties", propertyCompound);
        }

        return nbttagcompound;
    }

    @Nonnull
    public static IBlockState readBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf)
    {
        NBTTagCompound tag = buf.readCompoundTag();
        return tag != null
                ? readBlockState(registry, tag)
                : Blocks.AIR.getDefaultState();
    }

    public static void writeBlockState(@Nonnull MCRegistry registry, @Nonnull PacketBuffer buf, @Nonnull IBlockState state)
    {
        buf.writeCompoundTag(writeBlockState(registry, state));
    }

    public static IBlockState fromLegacyMetadata(String blockID, byte metadata)
    {
        int legacyBlockID = BlockStateFlatternEntities.getBlockId(blockID);
        Dynamic<?> dynamicNBT = BlockStateFlatteningMap.getFixedNBTForID((legacyBlockID << 4) + metadata & 15);
        NBTTagCompound compound = (NBTTagCompound) dynamicNBT.getValue();
        return NBTUtil.readBlockState(compound);
    }
}
