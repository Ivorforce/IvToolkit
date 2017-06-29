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

package ivorius.ivtoolkit.blocks;


import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class IvBlockMultiblock extends Block
{
    protected IvBlockMultiblock(Material par2Material)
    {
        super(par2Material);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof IvTileEntityMultiBlock)
        {
            IvTileEntityMultiBlock tileEntityMultiBlock = (IvTileEntityMultiBlock) tileEntity;

            if (!tileEntityMultiBlock.multiblockInvalid)
            {
                tileEntityMultiBlock.multiblockInvalid = true;

                if (tileEntityMultiBlock.isParent())
                    destroyChildrenOf(world, pos, state, tileEntityMultiBlock);
                else
                    destroyParentOf(tileEntityMultiBlock);
            }
        }

        super.breakBlock(world, pos, state);
    }

    public void destroyChildrenOf(World world, BlockPos pos, IBlockState state, IvTileEntityMultiBlock tileEntityMultiBlock)
    {
        if (!world.isRemote)
            this.parentBlockDropItemContents(world, tileEntityMultiBlock, pos, state);

        BlockPos[] toDestroy = tileEntityMultiBlock.getActiveChildCoords();
        TileEntity[] destroyTEs = new TileEntity[toDestroy.length];

        for (int i = 0; i < toDestroy.length; i++)
        {
            TileEntity otherTE = world.getTileEntity(toDestroy[i]);

            if (otherTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock) otherTE).multiblockInvalid)
                ((IvTileEntityMultiBlock) (destroyTEs[i] = otherTE)).multiblockInvalid = true;
        }

        for (TileEntity destroyTE : destroyTEs)
        {
            if (destroyTE != null)
            {
                triggerBlockDestroyEffects(world, destroyTE.getPos());
                world.setBlockToAir(destroyTE.getPos());
            }
        }
    }

    protected void triggerBlockDestroyEffects(World world, BlockPos pos)
    {
        world.playBroadcastSound(2001, pos, Block.getStateId(world.getBlockState(pos)));
//        if (world.isRemote)
//            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(parentTE.x, parentTE.y, parentTE.z, parentTE.getBlockType(), parentTE.getBlockMetadata());
    }

    public void destroyParentOf(IvTileEntityMultiBlock tileEntityMultiBlock)
    {
        World world = tileEntityMultiBlock.getWorld();
        TileEntity parentTE = world.getTileEntity(tileEntityMultiBlock.getActiveParentCoords());

        if (parentTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock) parentTE).multiblockInvalid)
        {
            triggerBlockDestroyEffects(world, parentTE.getPos());
            world.setBlockToAir(parentTE.getPos());
        }
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, pos);

        if (tileEntityMultiBlock != null)
        {
            if (!world.isRemote && willHarvest)
                this.parentBlockHarvestItem(world, tileEntityMultiBlock, pos, world.getBlockState(pos));
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void onBlockExploded(World world, BlockPos pos, Explosion explosion)
    {
        IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, pos);

        if (tileEntityMultiBlock != null)
        {
            if (!world.isRemote)
                this.parentBlockHarvestItem(world, tileEntityMultiBlock, pos, world.getBlockState(pos));
        }

        super.onBlockExploded(world, pos, explosion);
    }

    public void parentBlockDropItemContents(World world, IvTileEntityMultiBlock tileEntity, BlockPos pos, IBlockState state)
    {
    }

    public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, BlockPos pos, IBlockState state)
    {
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
    }

//    @Override
//    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
//    {
//        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
//
//        validateMultiblock(this, par1World, par2, par3, par4);
//    }

    public static boolean validateMultiblock(Block block, IBlockAccess access, BlockPos pos)
    {
        if (access.getBlockState(pos) != block)
            return false;

        boolean isValidChild = false;
        boolean destroy = false;

        TileEntity tileEntity = access.getTileEntity(pos);
        if (tileEntity instanceof IvTileEntityMultiBlock)
        {
            IvTileEntityMultiBlock tileEntityMultiBlock = (IvTileEntityMultiBlock) tileEntity;
            IvTileEntityMultiBlock parent = tileEntityMultiBlock.getParent();

            isValidChild = tileEntityMultiBlock.isParent() || (parent != null && parent.isParent());
            destroy = !isValidChild && !tileEntityMultiBlock.multiblockInvalid;
        }

        if (destroy)
        {
            if (access instanceof World)
                ((World) access).setBlockToAir(pos);
        }

        return isValidChild;
    }

    public static IvTileEntityMultiBlock getValidatedIfParent(Block block, World world, BlockPos pos)
    {
        if (validateMultiblock(block, world, pos))
        {
            IvTileEntityMultiBlock tileEntity = (IvTileEntityMultiBlock) world.getTileEntity(pos);
            return tileEntity.isParent() ? tileEntity : null;
        }

        return null;
    }

    public static IvTileEntityMultiBlock getValidatedTotalParent(Block block, IBlockAccess access, BlockPos pos)
    {
        if (validateMultiblock(block, access, pos))
        {
            TileEntity tileEntity = access.getTileEntity(pos);

            if (tileEntity instanceof IvTileEntityMultiBlock)
                return ((IvTileEntityMultiBlock) tileEntity).getTotalParent();
        }

        return null;
    }
}
