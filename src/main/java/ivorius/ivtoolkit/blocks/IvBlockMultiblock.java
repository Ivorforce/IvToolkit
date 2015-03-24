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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class IvBlockMultiblock extends BlockContainer
{
    protected IvBlockMultiblock(Material par2Material)
    {
        super(par2Material);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int blockMeta)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof IvTileEntityMultiBlock)
        {
            IvTileEntityMultiBlock tileEntityMultiBlock = (IvTileEntityMultiBlock) tileEntity;

            if (!tileEntityMultiBlock.multiblockInvalid)
            {
                tileEntityMultiBlock.multiblockInvalid = true;

                if (tileEntityMultiBlock.isParent())
                    destroyChildrenOf(world, x, y, z, block, blockMeta, tileEntityMultiBlock);
                else
                    destroyParentOf(tileEntityMultiBlock);
            }
        }

        super.breakBlock(world, x, y, z, block, blockMeta);
    }

    public void destroyChildrenOf(World world, int x, int y, int z, Block block, int blockMeta, IvTileEntityMultiBlock tileEntityMultiBlock)
    {
        if (!world.isRemote)
            this.parentBlockDropItemContents(world, tileEntityMultiBlock, x, y, z, block, blockMeta);

        int[][] toDestroy = tileEntityMultiBlock.getActiveChildCoords();
        TileEntity[] destroyTEs = new TileEntity[toDestroy.length];

        for (int i = 0; i < toDestroy.length; i++)
        {
            int[] coords = toDestroy[i];
            TileEntity otherTE = world.getTileEntity(coords[0], coords[1], coords[2]);

            if (otherTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock) otherTE).multiblockInvalid)
                ((IvTileEntityMultiBlock) (destroyTEs[i] = otherTE)).multiblockInvalid = true;
        }

        for (TileEntity destroyTE : destroyTEs)
        {
            if (destroyTE != null)
            {
                triggerBlockDestroyEffects(world, destroyTE.xCoord, destroyTE.yCoord, destroyTE.zCoord);
                world.setBlockToAir(destroyTE.xCoord, destroyTE.yCoord, destroyTE.zCoord);
            }
        }
    }

    protected void triggerBlockDestroyEffects(World world, int x, int y, int z)
    {
        world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(this) + (world.getBlockMetadata(x, y, z) << 12));
//        if (world.isRemote)
//            Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(parentTE.xCoord, parentTE.yCoord, parentTE.zCoord, parentTE.getBlockType(), parentTE.getBlockMetadata());
    }

    public void destroyParentOf(IvTileEntityMultiBlock tileEntityMultiBlock)
    {
        World world = tileEntityMultiBlock.getWorldObj();
        int[] parentTECoords = tileEntityMultiBlock.getActiveParentCoords();
        TileEntity parentTE = world.getTileEntity(parentTECoords[0], parentTECoords[1], parentTECoords[2]);

        if (parentTE instanceof IvTileEntityMultiBlock && !((IvTileEntityMultiBlock) parentTE).multiblockInvalid)
        {
            triggerBlockDestroyEffects(world, parentTE.xCoord, parentTE.yCoord, parentTE.zCoord);
            world.setBlockToAir(parentTE.xCoord, parentTE.yCoord, parentTE.zCoord);
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest)
    {
        IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, x, y, z);

        if (tileEntityMultiBlock != null)
        {
            if (!world.isRemote && willHarvest)
                this.parentBlockHarvestItem(world, tileEntityMultiBlock, x, y, z, this, world.getBlockMetadata(x, y, z));
        }

        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void onBlockExploded(World world, int x, int y, int z, Explosion explosion)
    {
        IvTileEntityMultiBlock tileEntityMultiBlock = getValidatedTotalParent(this, world, x, y, z);

        if (tileEntityMultiBlock != null)
        {
            if (!world.isRemote)
                this.parentBlockHarvestItem(world, tileEntityMultiBlock, x, y, z, this, world.getBlockMetadata(x, y, z));
        }

        super.onBlockExploded(world, x, y, z, explosion);
    }

    public void parentBlockDropItemContents(World world, IvTileEntityMultiBlock tileEntity, int x, int y, int z, Block block, int metadata)
    {
    }

    public void parentBlockHarvestItem(World world, IvTileEntityMultiBlock tileEntity, int x, int y, int z, Block block, int metadata)
    {
    }

    @Override
    public void dropBlockAsItemWithChance(World par1World, int par2, int par3, int par4, int par5, float par6, int par7)
    {

    }

//    @Override
//    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
//    {
//        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
//
//        validateMultiblock(this, par1World, par2, par3, par4);
//    }

    public static boolean validateMultiblock(Block block, IBlockAccess access, int x, int y, int z)
    {
        if (access.getBlock(x, y, z) != block)
        {
            return false;
        }

        boolean isValidChild = false;
        boolean destroy = false;

        TileEntity tileEntity = access.getTileEntity(x, y, z);
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
                ((World) access).setBlockToAir(x, y, z);
        }

        return isValidChild;
    }

    public static IvTileEntityMultiBlock getValidatedIfParent(Block block, World world, int x, int y, int z)
    {
        if (validateMultiblock(block, world, x, y, z))
        {
            IvTileEntityMultiBlock tileEntity = (IvTileEntityMultiBlock) world.getTileEntity(x, y, z);
            return tileEntity.isParent() ? tileEntity : null;
        }

        return null;
    }

    public static IvTileEntityMultiBlock getValidatedTotalParent(Block block, IBlockAccess access, int x, int y, int z)
    {
        if (validateMultiblock(block, access, x, y, z))
        {
            TileEntity tileEntity = access.getTileEntity(x, y, z);

            if (tileEntity instanceof IvTileEntityMultiBlock)
                return ((IvTileEntityMultiBlock) tileEntity).getTotalParent();
        }

        return null;
    }
}
