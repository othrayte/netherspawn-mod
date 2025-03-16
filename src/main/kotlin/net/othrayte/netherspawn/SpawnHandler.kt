package net.othrayte.netherspawn

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.level.Level.NETHER
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FallingBlock
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent
import net.othrayte.netherspawn.NetherSpawn.LOGGER
import net.othrayte.netherspawn.extentions.BoundingBoxEx.positions
import net.othrayte.netherspawn.extentions.DimensionTransitionEx.withLocation
import net.othrayte.netherspawn.extentions.PlayerEx.isFirstLogin
import org.apache.logging.log4j.Level

@EventBusSubscriber(modid = NetherSpawn.ID)
object SpawnHandler {
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            // Detect if this is the first time we have observed this player logging in
            // If they have a spawn point, then just let them log in normally, otherwise teleport them to the nether as
            // this should be their first login with the mod installed
            if (player.isFirstLogin() && player.respawnPosition == null) {
                LOGGER.log(Level.INFO, "Detected first login for player ${player.name}, moving to nether spawn")
                val nether = player.server.getLevel(NETHER)!!
                val spawnLocation = Nether.locateNetherSpawnNear(BlockPos(0, nether.seaLevel + 5, 0), nether)?.pos
                    ?: Nether.locateCentralNetherSpawn(
                        nether
                    )
                val point = spawnLocation.bottomCenter
                player.teleportTo(nether, point.x, point.y, point.z, 0f, 0f)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerRespawnPositionEvent(event: PlayerRespawnPositionEvent) {
        val player = event.entity
        // Only handle the case of a server player as this needs to be handled on the server side
        if (player is ServerPlayer) {
            // Check if the player is respawning due to missing bed/respawn anchor or if they have no respawn point
            if (event.originalDimensionTransition.missingRespawnBlock || player.respawnPosition == null) {
                val server = event.originalDimensionTransition.newLevel.server
                val nether = server.getLevel(NETHER)!!

                val respawnPos = Nether.locateNetherSpawnNear(BlockPos(0, nether.seaLevel + 5, 0), nether)

                if (respawnPos != null) {
                    event.dimensionTransition =
                        event.originalDimensionTransition.withLocation(nether, respawnPos.pos.bottomCenter)

                    // Make one unsupported obsidian block fall
                    val structureBlocks = respawnPos.structureBounds.positions()
                    val obsidianBlocks = structureBlocks.filter { pos ->
                        nether.getBlockState(pos).`is`(net.neoforged.neoforge.common.Tags.Blocks.OBSIDIANS)
                    }
                    val unsupportedObsidian = obsidianBlocks.filter { pos ->
                        val blockUnder = nether.getBlockState(pos.below())
                        FallingBlock.isFree(blockUnder)
                    }.firstOrNull()
                    if (unsupportedObsidian != null) {
                        FallingBlockEntity.fall(nether, unsupportedObsidian, nether.getBlockState(unsupportedObsidian))
                    } else {
                        // If no unsupported obsidian blocks are found, remove one obsidian block
                        val obsidianToRemove = obsidianBlocks.shuffled().firstOrNull()
                        if (obsidianToRemove != null) {
                            nether.setBlockAndUpdate(obsidianToRemove, Blocks.BLACKSTONE.defaultBlockState())
                        }
                    }
                } else {
                    val spawnLocation = Nether.locateCentralNetherSpawn(nether)

                    event.dimensionTransition =
                        event.originalDimensionTransition.withLocation(nether, spawnLocation.bottomCenter)
                }
            }
        }
    }

}