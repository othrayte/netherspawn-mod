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
        if (player is ServerPlayer && Options.initialSpawnInNether) {
            // Detect if this is the first time we have observed this player logging in
            // If they have a spawn point, then just let them log in normally, otherwise teleport them to the nether as
            // this should be their first login with the mod installed
            if (player.isFirstLogin() && player.respawnPosition == null) {
                LOGGER.log(Level.INFO, "Detected first login for player ${player.name}, moving to nether spawn")
                val nether = player.server.getLevel(NETHER)!!


                val ruinedPortalSpawn = if (Options.RuinedPortalSpawn.enable) Nether.locateNetherSpawnNear(
                    BlockPos(0, nether.seaLevel + 5, 0),
                    nether
                )
                else null
                val spawnLocation = ruinedPortalSpawn?.pos
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
        if (player is ServerPlayer && Options.respawnInNether) {
            // Check if the player is respawning due to missing bed/respawn anchor or if they have no respawn point
            if (event.originalDimensionTransition.missingRespawnBlock || player.respawnPosition == null) {
                val server = event.originalDimensionTransition.newLevel.server
                val nether = server.getLevel(NETHER)!!

                val searchCenter =
                    if (Options.RuinedPortalSpawn.closestToDeathLocation && player.serverLevel() == nether) {
                        player.blockPosition()
                    } else {
                        BlockPos(0, nether.seaLevel + 5, 0)
                    }
                val respawnPos = if (Options.RuinedPortalSpawn.enable) {
                    Nether.locateNetherSpawnNear(searchCenter, nether)
                } else {
                    null
                }

                if (respawnPos != null) {
                    event.dimensionTransition =
                        event.originalDimensionTransition.withLocation(nether, respawnPos.pos.bottomCenter)

                    if (Options.RuinedPortalSpawn.blocksDamagedOnRespawn > 0) {
                        // Make one unsupported obsidian block fall
                        val structureBlocks = respawnPos.structureBounds.positions()

                        val obsidianBlocks = structureBlocks.filter { pos ->
                            nether.getBlockState(pos).`is`(net.neoforged.neoforge.common.Tags.Blocks.OBSIDIANS)
                        }
                        val unsupportedObsidian =
                            if (Options.RuinedPortalSpawn.portalBlocksFallOnDamage) {
                                obsidianBlocks.filter { pos ->
                                    val blockUnder = nether.getBlockState(pos.below())
                                    FallingBlock.isFree(blockUnder)
                                }.take(Options.RuinedPortalSpawn.blocksDamagedOnRespawn).toList()
                            } else {
                                emptyList()
                            }

                        obsidianBlocks.shuffled()
                            .take(Options.RuinedPortalSpawn.blocksDamagedOnRespawn - unsupportedObsidian.size)
                            .forEach {
                                nether.setBlockAndUpdate(it, Blocks.BLACKSTONE.defaultBlockState())
                            }

                        unsupportedObsidian.forEach {
                            FallingBlockEntity.fall(nether, it, nether.getBlockState(it))
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