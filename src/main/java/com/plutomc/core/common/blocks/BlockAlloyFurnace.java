package com.plutomc.core.common.blocks;

import com.plutomc.core.common.tileentities.TileEntityAlloyFurnace;
import com.plutomc.core.init.BlockRegistry;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * plutomc_core
 * Copyright (C) 2016  Kevin Boxhoorn
 *
 * plutomc_core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * plutomc_core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with plutomc_core.  If not, see <http://www.gnu.org/licenses/>.
 */
public class BlockAlloyFurnace extends BaseBlock
{
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool BURNING = PropertyBool.create("burning");

	public BlockAlloyFurnace()
	{
		super(BlockRegistry.Data.ALLOY_FURNACE);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BURNING, false));
		setHardness(3.5f);
		setSoundType(SoundType.STONE);
		isBlockContainer = true;
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING, BURNING);
	}

	@Nonnull
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.fromAngle(placer.getRotationYawHead()).getOpposite()).withProperty(BURNING, false);
	}

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta & 7)).withProperty(BURNING, (meta & 8) > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int i = 0;
		i |= state.getValue(FACING).getIndex();
		if (state.getValue(BURNING))
		{
			i |= 8;
		}
		return i;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityAlloyFurnace();
	}

	@Override
	public boolean hasTileEntity(IBlockState state)
	{
		return true;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		super.breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntityAlloyFurnace)
		{
			InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityAlloyFurnace) tileEntity);
			worldIn.updateComparatorOutputLevel(pos, this);
		}
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return BlockRegistry.ALLOY_FURNACE;
	}

	public static void setBurningAtPos(World worldIn, BlockPos pos, boolean burning)
	{
		IBlockState blockState = worldIn.getBlockState(pos);
		IBlockState defaultState = BlockRegistry.ALLOY_FURNACE.getBlock().getDefaultState();
		worldIn.setBlockState(pos, defaultState.withProperty(FACING, blockState.getValue(FACING)).withProperty(BURNING, burning), 3);

		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity != null)
		{
			tileEntity.validate();
			worldIn.setTileEntity(pos, tileEntity);
		}
	}
}