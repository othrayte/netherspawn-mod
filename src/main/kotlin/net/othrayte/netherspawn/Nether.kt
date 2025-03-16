package net.othrayte.netherspawn

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.othrayte.netherspawn.extentions.BoundingBoxEx.centerOutInLayersDownUp
import net.othrayte.netherspawn.extentions.BoundingBoxEx.positions
import net.othrayte.netherspawn.extentions.LevelEx.nearestRuinedPortals
import net.othrayte.netherspawn.extentions.LevelEx.playerCanSpawnAt
import net.othrayte.netherspawn.extentions.SequenceEx.weightedRandom
import kotlin.math.abs

object Nether {
    // Locates a suitable spawn at a ruined portal near the given position
    fun locateNetherSpawnNear(pos: BlockPos, nether: ServerLevel): RuinedPortalSpawn? {
        // Locate the nearest ruined portal in the nether using the structure locator
        val respawnPos = nether.nearestRuinedPortals(pos, 100).filter { portal ->
                // Filter out any portals that have no obsidian blocks
                portal.structureBounds.positions().any { pos ->
                    nether.getBlockState(pos).`is`(net.neoforged.neoforge.common.Tags.Blocks.OBSIDIANS)
                }
            }.map { nonRemovedPortal ->
                // Set the respawn position to the ruined portal
                val validSpawnLocations =
                    nonRemovedPortal.structureBounds.centerOutInLayersDownUp().withIndex().filter { indexedPos ->
                        nether.playerCanSpawnAt(indexedPos.value)
                    }

                val spawnPoint = validSpawnLocations.weightedRandom({
                    100 / (nonRemovedPortal.structureBounds.center.distSqr(it.value).toInt() + 1)
                })?.value
                if (spawnPoint != null) {
                    RuinedPortalSpawn(spawnPoint, nonRemovedPortal.structureBounds)
                } else {
                    null
                }
            }.filterNotNull().firstOrNull()

        return respawnPos
    }

    // Locates a suitable spawn in the nether near (0,0)
    fun locateCentralNetherSpawn(nether: ServerLevel): BlockPos {
        // Search for a valid spawn location near the center of the nether
        // First try x0, z0 and search from middle outwards looking for a valid spawn location
        val centerChunk = nether.getChunk(0, 0)
        val minY = nether.minBuildHeight
        val maxY = nether.minBuildHeight + nether.logicalHeight - 1
        val centerY = nether.logicalHeight / 2 + centerChunk.minBuildHeight
        val origin = BlockPos(0, centerY, 0)
        // Create a sequence of positions, start with positions of increasing manhattan distance from the
        // center at centerY and for each increase in distance also try all positions below and above of
        // decreasing manhattan distance, thus preferencing points closer to the center and closer to
        // centerY

        // First create a sequence generator for one layer of positions at the same manhattan distance
        val manhattanDiamond = { manhattanDist: Int, y: Int ->
            sequence {
                for (z in -manhattanDist..manhattanDist) {
                    val x = manhattanDist - abs(z)
                    yield(BlockPos(x, y, z))
                    if (x > 0) yield(BlockPos(-x, y, z))
                }
            }
        }

        // Then create a sequence generator for manhattan diamond shells
        val manhattanShells = { manhattanDist: Int ->
            sequence {
                for (r in 0..manhattanDist) {
                    val yUp = centerY + r
                    val yDown = centerY - r
                    if (yUp <= maxY) yieldAll(manhattanDiamond(manhattanDist - r, yUp))
                    if (yDown != yUp && yDown >= minY) yieldAll(manhattanDiamond(manhattanDist - r, yDown))
                }
            }
        }

        val spawnLocation = sequence {
            for (shellDist in 0..5000 step 5) {
                yieldAll(manhattanShells(shellDist))
            }
        }.filter { pos ->
            nether.playerCanSpawnAt(pos)
        }.filterIndexed { index: Int, _: BlockPos ->
            index.mod(27) == 0
        }.take(100).shuffled().firstOrNull() ?: origin
        return spawnLocation
    }
}