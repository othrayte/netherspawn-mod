package net.othrayte.netherspawn.extentions

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags.INVALID_SPAWN_INSIDE
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.level.levelgen.structure.BuiltinStructures
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.level.levelgen.structure.StructureCheckResult
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.othrayte.netherspawn.RuinedPortalSpawn
import net.othrayte.netherspawn.Tags
import net.othrayte.netherspawn.extentions.ChunkGeneratorStructureStateEx.getPlacementsForStructure
import net.othrayte.netherspawn.extentions.VoxelShapeEx.intersects

object LevelEx {
    // Check if the player can spawn at the given position
    fun BlockGetter.playerCanSpawnAt(feetPos: BlockPos): Boolean {
        val blockUnderFeet = getBlockState(feetPos.below())
        val canStandOn = !blockUnderFeet.isAir && (blockUnderFeet.isFaceSturdy(
            this,
            feetPos.below(),
            Direction.UP
        ) || blockUnderFeet.getCollisionShape(this, feetPos.below()).intersects(
                AABB.ofSize(
                    Vec3(0.5, 0.5, 0.5),
                    0.5,
                    1.0,
                    0.5
                )
            )) && !blockUnderFeet.`is`(Tags.Blocks.DAMAGES_PLAYER)
        val blockStateAtFeet = getBlockState(feetPos)
        val headPos = feetPos.above()
        val blockStateAtHead = getBlockState(headPos)
        return canStandOn && (!blockStateAtFeet.`is`(Tags.Blocks.DAMAGES_PLAYER) && blockStateAtFeet.getCollisionShape(
            this,
            feetPos
        ).isEmpty && !blockStateAtFeet.`is`(INVALID_SPAWN_INSIDE)) && (!blockStateAtHead.`is`(Tags.Blocks.DAMAGES_PLAYER) && blockStateAtHead.getCollisionShape(
            this,
            headPos
        ).isEmpty && !blockStateAtHead.`is`(INVALID_SPAWN_INSIDE) && !blockStateAtHead.isSuffocating(this, headPos))
    }

    // Get the chunk at the given position
    fun LevelReader.getChunk(chunkPos: ChunkPos, chunkStatus: ChunkStatus): ChunkAccess {
        return getChunk(chunkPos.x, chunkPos.z, chunkStatus)
    }

    // Get the ruined portal structure instance
    fun LevelAccessor.getRuinedPortal(): Structure {
        val registry = registryAccess().registryOrThrow(Registries.STRUCTURE)
        return registry.getHolder(BuiltinStructures.RUINED_PORTAL_NETHER).get().value()
    }

    // Get the nearest ruined portal to the given position (within the limited search radius)
    fun ServerLevel.nearestRuinedPortals(pos: BlockPos, searchRadius: Int): Sequence<RuinedPortalSpawn> {
        val ruinedPortal = level.getRuinedPortal()

        val placements = level.chunkSource.generatorState.getPlacementsForStructure(ruinedPortal)
        assert(placements.size == 1)
        assert(placements[0] is RandomSpreadStructurePlacement)
        val placement = placements[0] as RandomSpreadStructurePlacement

        val sectionX = SectionPos.blockToSectionCoord(pos.x)
        val sectionZ = SectionPos.blockToSectionCoord(pos.z)

        val seed = level.chunkSource.generatorState.levelSeed
        val spacing = placement.spacing()
        val potentialChunks = sequence {
            for (r in 0..searchRadius) {
                for (xStep in -r..r) {
                    val zEdge = xStep == -r || xStep == r
                    for (zStep in -r..r) {
                        val xEdge = zStep == -r || zStep == r
                        if (zEdge || xEdge) {
                            val candidateSectionX = sectionX + spacing * xStep
                            val candidateSectionZ = sectionZ + spacing * zStep
                            val chunkPos =
                                placement.getPotentialStructureChunk(seed, candidateSectionX, candidateSectionZ)
                            yield(chunkPos)
                        }
                    }
                }
            }
        }
        val structurePositions = potentialChunks.map { potentialChunk ->
            val presence = level.structureManager().checkStructurePresence(
                potentialChunk, ruinedPortal, placement, false
            )
            if (presence == StructureCheckResult.START_NOT_PRESENT) return@map null
            val chunk = level.getChunk(potentialChunk, ChunkStatus.STRUCTURE_STARTS)
            val structureStart = level.structureManager().getStartForStructure(
                SectionPos.bottomOf(chunk), ruinedPortal, chunk
            ) ?: return@map null
            if (!structureStart.isValid) return@map null
            RuinedPortalSpawn(placement.getLocatePos(structureStart.chunkPos), structureStart.boundingBox)
        }.filterNotNull()
        return structurePositions
    }
}