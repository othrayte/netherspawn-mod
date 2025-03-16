package net.othrayte.netherspawn.extentions

import net.minecraft.core.Holder
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement

object ChunkGeneratorStructureStateEx {
    // Get the placements for the given structure
    fun ChunkGeneratorStructureState.getPlacementsForStructure(structure: Structure): List<StructurePlacement> {
        return getPlacementsForStructure(Holder.direct(structure))
    }
}