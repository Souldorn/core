package com.plutomc.core.common.blocks;

import com.plutomc.core.common.blocks.properties.PredicateAxisOrientation;
import com.plutomc.core.common.tileentities.TileEntityUnderworldGate;
import com.plutomc.core.init.BlockRegistry;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
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
public class BlockUnderworldGate extends BaseBlock implements ITileEntityProvider
{
	public enum EnumSubBlock implements IStringSerializable
	{
		BOTTOM_LEFT(0, "bottom_left", 0, 0),
		BOTTOM_RIGHT(1, "bottom_right", 1, 0),
		CENTER_LEFT(2, "center_left", 0, 1),
		CENTER_RIGHT(3, "center_right", 1, 1),
		TOP_LEFT(4, "top_left", 0, 2),
		TOP_RIGHT(5, "top_right", 1, 2);

		private final int index;
		private final String name;
		private final BlockPos pos;

		EnumSubBlock(int index, String name, int x, int y)
		{
			this.index = index;
			this.name = name;
			this.pos = new BlockPos(x, y, 0);
		}

		public int getIndex()
		{
			return index;
		}

		@Nonnull
		@Override
		public String getName()
		{
			return name;
		}

		public BlockPos getPos()
		{
			return pos;
		}

		public static EnumSubBlock fromIndex(int index)
		{
			return values()[MathHelper.abs(index % values().length)];
		}

		public boolean isRender()
		{
			return this == BOTTOM_LEFT;
		}
	}

	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, PredicateAxisOrientation.HORIZONTAL);
	public static final PropertyEnum<EnumSubBlock> SUBBLOCK = PropertyEnum.create("subblock", EnumSubBlock.class);

	private static final AxisAlignedBB X_AABB = new AxisAlignedBB(0d, 0d, 9d * PIXEL_SIZE, 1d, 1d, 7d * PIXEL_SIZE);
	private static final AxisAlignedBB Z_AABB = new AxisAlignedBB(7d * PIXEL_SIZE, 0d, 1d, 9d * PIXEL_SIZE, 1d, 0d);
	private static final AxisAlignedBB X_RENDER_AABB = new AxisAlignedBB(0d, 0d, 9d * PIXEL_SIZE, 2d, 3d, 7d * PIXEL_SIZE);
	private static final AxisAlignedBB Z_RENDER_AABB = new AxisAlignedBB(7d * PIXEL_SIZE, 0d, 2d, 9d * PIXEL_SIZE, 3d, 0d);

	public BlockUnderworldGate()
	{
		super(BlockRegistry.Data.UNDERWORLD_GATE);
		setDefaultState(blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(SUBBLOCK, EnumSubBlock.BOTTOM_LEFT));
		setBlockUnbreakable();
		setLightLevel(0.075f);
		setLightOpacity(0);
	}

	@Nonnull
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, AXIS, SUBBLOCK);
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(AXIS, (meta & 1) == 0 ? EnumFacing.Axis.X : EnumFacing.Axis.Z).withProperty(SUBBLOCK, EnumSubBlock.fromIndex((meta & 14) >> 1));
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		int result = 0;
		result |= (state.getValue(AXIS) == EnumFacing.Axis.X ? 0 : 1);
		result |= (state.getValue(SUBBLOCK).getIndex() << 1);
		return result;
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		if (state.getValue(SUBBLOCK).isRender())
		{
			worldIn.removeTileEntity(pos);
		}
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntityUnderworldGate(getData());
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return state.getValue(AXIS) == EnumFacing.Axis.X ? X_AABB : Z_AABB;
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return NULL_AABB;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		return (state.getValue(AXIS) == EnumFacing.Axis.X ? X_RENDER_AABB : Z_RENDER_AABB).offset(pos);
	}

	@Nonnull
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
	{
		return Item.getItemFromBlock(Blocks.AIR);
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		// TODO: Teleport to Underworld dimension.
	}

	public static void create(World world, BlockPos pos, EnumFacing.Axis axis)
	{
		IBlockState state = BlockRegistry.UNDERWORLD_GATE.getBlock().getDefaultState().withProperty(AXIS, axis);

		for (EnumSubBlock subBlock : EnumSubBlock.values())
		{
			BlockPos subBlockPos = axis == EnumFacing.Axis.X ? subBlock.getPos() : subBlock.getPos().rotate(Rotation.CLOCKWISE_90).north();
			world.setBlockState(pos.add(subBlockPos), state.withProperty(SUBBLOCK, subBlock), 3);
		}
	}
}
