package com.plutomc.core.common.world.gen;

import com.plutomc.core.common.blocks.BlockLeadOre;
import com.plutomc.core.init.WorldRegistry;
import net.minecraft.block.state.IBlockState;

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
public class WorldGenLeadOre extends WorldGenOre
{
	public WorldGenLeadOre()
	{
		super(WorldRegistry.OreData.LEAD_ORE);
	}

	@Override
	public IBlockState getBlockForPlacement(Random rand)
	{
		return getBlock().withProperty(BlockLeadOre.HAS_CROCOITE, rand.nextInt(4) == 0);
	}
}
