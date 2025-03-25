package net.othrayte.netherspawn

import net.neoforged.neoforge.common.ModConfigSpec

// Options for the mod
object Options {
    private val builder = ModConfigSpec.Builder()

    private val initialSpawnInNetherOption = builder
        .comment("Whether players should initially spawn in the nether")
        .define("initial_spawn_in_nether", false)
    private val respawnInNetherOption = builder
        .comment("Whether players should respawn in the nether when they die in the nether")
        .define("respawn_in_nether", true)
    private val randomiseSpawnLocationOption = builder
        .comment("Whether the block to spawn on should be randomised, otherwise the first valid block is used")
        .define("randomise_spawn_location", true)

    val initialSpawnInNether: Boolean get() = initialSpawnInNetherOption.get()
    val respawnInNether: Boolean get() = respawnInNetherOption.get()
    val randomiseSpawnLocation: Boolean get() = randomiseSpawnLocationOption.get()

    private val ruinedPortalSpawnEnableOption = builder
        .comment("Whether to try to spawn players at a ruined portal")
        .define("ruined_portal_spawn.enable", true)
    private val ruinedPortalSpawnSearchRadiusOption = builder
        .comment("The radius to search for a ruined portal")
        .defineInRange("ruined_portal_spawn.search_radius", 100, 0, Int.MAX_VALUE)
    private val ruinedPortalSpawnClosestToDeathLocationOption = builder
        .comment("Whether to try to spawn players at the nearest ruined portal to their death location")
        .define("ruined_portal_spawn.closest_to_death_location", false)
    private val ruinedPortalSpawnPreferLocationsNearCenterOption = builder
        .comment("Whether to prefer blocks near the center of the ruined portal. randomise_spawn_location implies true")
        .define("ruined_portal_spawn.prefer_locations_near_center", true)
    private val ruinedPortalSpawnExpandStructureSearchBoundsOption = builder
        .comment("The number of blocks to expand the search bounds for a ruined portal")
        .defineInRange("ruined_portal_spawn.expand_structure_search_bounds", 0, 0, 99)
    private val ruinedPortalSpawnBlocksDamagedOnRespawnOption = builder
        .comment("The number of blocks to damage when a player respawns at a ruined portal")
        .defineInRange("ruined_portal_spawn.blocks_damaged_on_respawn", 1, 0, Int.MAX_VALUE)
    private val ruinedPortalSpawnPortalBlocksFallOnDamageOption = builder
        .comment("Whether the portal blocks should fall before being degraded.")
        .define("ruined_portal_spawn.portal_blocks_fall", true)
    private val ruinedPortalSpawnPortalBlocksRequiredForRespawnOption = builder
        .comment("The number of portal blocks required for a player to respawn at a ruined portal")
        .defineInRange("ruined_portal_spawn.portal_blocks_required_for_respawn", 1, 0, Int.MAX_VALUE)
    object RuinedPortalSpawn {
        val enable: Boolean get() = ruinedPortalSpawnEnableOption.get()
        val searchRadius: Int get() = ruinedPortalSpawnSearchRadiusOption.get()
        val closestToDeathLocation: Boolean get() = ruinedPortalSpawnClosestToDeathLocationOption.get()
        val preferLocationsNearCenter: Boolean get() = ruinedPortalSpawnPreferLocationsNearCenterOption.get()
        val expandStructureSearchBounds: Int get() = ruinedPortalSpawnExpandStructureSearchBoundsOption.get()
        val blocksDamagedOnRespawn: Int get() = ruinedPortalSpawnBlocksDamagedOnRespawnOption.get()
        val portalBlocksFallOnDamage: Boolean get() = ruinedPortalSpawnPortalBlocksFallOnDamageOption.get()
        val portalBlocksRequiredForRespawn: Int get() = ruinedPortalSpawnPortalBlocksRequiredForRespawnOption.get()
    }

    private val centralNetherSpawnMaxSearchDistOption = builder
        .comment("The maximum manhattan distance from the center of the nether to search for a spawn location when using a central spawn")
        .defineInRange("central_nether_spawn.max_search_dist", 5000, 0, Int.MAX_VALUE)
    private val centralNetherSpawnSearchDistanceIncrementOption = builder
        .comment("Amount to increment the search distance each time a shell of blocks at one distance is searched. Increasing this value cuts down the number of similar spawn locations")
        .defineInRange("central_nether_spawn.search_distance_increment", 5, 1, Int.MAX_VALUE)
    private val centralNetherSpawnSearchSampleSkipOption = builder
        .comment("The number of valid spawn locations to skip for each one selected, 0 means no skipping. Increasing this value cuts down the number of similar spawn locations")
        .defineInRange("central_nether_spawn.search_sample_rate", 26, 0, Int.MAX_VALUE)
    private val centralNetherSpawnSearchSampleSizeOption = builder
        .comment("The number of spawn candidate to collect before randomly selecting one")
        .defineInRange("central_nether_spawn.search_sample_size", 100, 1, Int.MAX_VALUE)
    object CentralNetherSpawn {
        val maxSearchDist: Int get() = centralNetherSpawnMaxSearchDistOption.get()
        val searchDistanceIncrement: Int get() = centralNetherSpawnSearchDistanceIncrementOption.get()
        val searchSampleSkip: Int get() = centralNetherSpawnSearchSampleSkipOption.get()
        val searchSampleSize: Int get() = centralNetherSpawnSearchSampleSizeOption.get()
    }

    val CONFIG_SPEC: ModConfigSpec = builder.build()
}