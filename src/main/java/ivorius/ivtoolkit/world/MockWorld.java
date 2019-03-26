/*
 * Copyright 2017 Lukas Tenbrink
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

package ivorius.ivtoolkit.world;

import ivorius.ivtoolkit.blocks.IvTileEntityHelper;
import ivorius.ivtoolkit.tools.IvWorldData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Fluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by lukas on 14.04.17.
 */
public interface MockWorld extends IBlockReader
{
    static Real of(World world)
    {
        return new Real(world);
    }

    World asWorld();

    boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags);

    @Nonnull
    IBlockState getBlockState(@Nonnull BlockPos pos);

    @Nullable
    TileEntity getTileEntity(@Nonnull BlockPos pos);

    void setTileEntity(@Nonnull BlockPos pos, @Nullable TileEntity tileEntity);

    List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate);

    boolean addEntity(Entity entity);

    boolean removeEntity(Entity entity);

    Random rand();

    default boolean setBlockState(BlockPos coord, IBlockState block)
    {
        return setBlockState(coord, block, 3);
    }

    @OnlyIn(Dist.CLIENT)
    default int getCombinedLight(BlockPos pos, int lightValue)
    {
        return 0;
    }

    default boolean isAirBlock(BlockPos pos)
    {
        IBlockState state = getBlockState(pos);
        return state.getBlock().isAir(state, this, pos);
    }

    @OnlyIn(Dist.CLIENT)
    default Biome getBiome(BlockPos pos)
    {
        return Biomes.DEFAULT;
    }


    default int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    default WorldType getWorldType()
    {
        return WorldType.CUSTOMIZED;
    }

    class Real implements MockWorld
    {
        public World world;

        public Real(World world)
        {
            this.world = world;
        }

        @Override
        public World asWorld()
        {
            return world;
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags)
        {
            return world.setBlockState(pos, state, flags);
        }

        @Nonnull
        @Override
        public IBlockState getBlockState(@Nonnull BlockPos pos)
        {
            return world.getBlockState(pos);
        }

        @Override
        public IFluidState getFluidState(BlockPos pos)
        {
            return world.getFluidState(pos);
        }

        @Override
        public int getMaxLightLevel()
        {
            return world.getMaxLightLevel();
        }

        @Override
        public TileEntity getTileEntity(@Nonnull BlockPos pos)
        {
            return world.getTileEntity(pos);
        }

        @Override
        public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity)
        {
            world.setTileEntity(pos, tileEntity);
        }

        @Override
        public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate)
        {
            return world.getEntitiesInAABBexcluding(null, bounds, predicate != null ? predicate::test : null);
        }

        @Override
        public boolean addEntity(Entity entity)
        {
            return world.spawnEntity(entity);
        }

        @Override
        public boolean removeEntity(Entity entity)
        {
            world.removeEntity(entity);
            return true;
        }

        @Override
        public Random rand()
        {
            return world.rand;
        }

        @Override
        public int getCombinedLight(BlockPos pos, int lightValue)
        {
            return world.getCombinedLight(pos, lightValue);
        }

        @Override
        public Biome getBiome(BlockPos pos)
        {
            return world.getBiome(pos);
        }

        @Override
        public int getStrongPower(BlockPos pos, EnumFacing direction)
        {
            return world.getStrongPower(pos, direction);
        }

        @Override
        public WorldType getWorldType()
        {
            return world.getWorldType();
        }
    }

    class Cache extends Real
    {
        public WorldCache cache;

        public Cache(WorldCache cache)
        {
            super(cache.world);
            this.cache = cache;
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags)
        {
            return cache.setBlockState(pos, state, flags);
        }

        @Nonnull
        @Override
        public IBlockState getBlockState(@Nonnull BlockPos pos)
        {
            return cache.getBlockState(pos);
        }
    }

    class WorldData implements MockWorld
    {
        public IvWorldData worldData;
        public Random random = new Random();

        public WorldData(IvWorldData worldData)
        {
            this.worldData = worldData;
        }

        public static boolean isAt(@Nonnull BlockPos pos, NBTTagCompound nbt)
        {
            return pos.getX() == nbt.getInt("x")
                    && pos.getY() == nbt.getInt("y")
                    && pos.getZ() == nbt.getInt("z");
        }

        @Override
        public World asWorld()
        {
            throw new VirtualWorldException();
        }

        @Override
        public boolean setBlockState(@Nonnull BlockPos pos, @Nonnull IBlockState state, int flags)
        {
            worldData.blockCollection.setBlockState(pos, state);

            return true;
        }

        @Nonnull
        @Override
        public IBlockState getBlockState(@Nonnull BlockPos pos)
        {
            return worldData.blockCollection.getBlockState(pos);
        }

        @Override
        public IFluidState getFluidState(BlockPos pos)
        {
            return Fluids.EMPTY.getDefaultState();
        }

        @Override
        public TileEntity getTileEntity(@Nonnull BlockPos pos)
        {
            for (NBTTagCompound nbt : worldData.tileEntities) {
                if (isAt(pos, nbt))
                    return TileEntity.create(nbt);
            }

            return null;
        }

        @Override
        public void setTileEntity(@Nonnull BlockPos pos, TileEntity tileEntity)
        {
            worldData.tileEntities.removeIf(nbt -> isAt(pos, nbt));
            worldData.tileEntities.add(tileEntity.write(new NBTTagCompound()));
        }

        @Override
        public List<Entity> getEntities(AxisAlignedBB bounds, @Nullable Predicate<? super Entity> predicate)
        {
            return worldData.entities.stream()
                    .filter(nbt ->
                    {
                        NBTTagList pos = nbt.getList("Pos", 6);
                        return bounds.contains(new Vec3d(pos.getDouble(0), pos.getDouble(1), pos.getDouble(2)));
                    })
                    .map(nbt -> EntityType.create(nbt, IvTileEntityHelper.getAnyWorld()))
                    .filter(predicate)
                    .collect(Collectors.toList());
        }

        @Override
        public boolean addEntity(Entity entity)
        {
            // To make sure we don't have doubles
            removeEntity(entity);

            NBTTagCompound compound = new NBTTagCompound();
            if (!entity.writeUnlessRemoved(compound)) {
                return false;
            }

            return worldData.entities.add(compound);

        }

        @Override
        public boolean removeEntity(Entity entity)
        {
            return worldData.entities.removeIf(nbt -> entity.getUniqueID().equals(nbt.getUniqueId("UUID")));
        }

        @Override
        public Random rand()
        {
            return random;
        }
    }

    class VirtualWorldException extends RuntimeException
    {

    }
}
