package net.othrayte.netherspawn

import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox

// A potential spawn location within a ruined portal structure
data class RuinedPortalSpawn(val pos: BlockPos, val structureBounds: BoundingBox)