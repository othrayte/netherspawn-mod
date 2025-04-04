package net.othrayte.netherspawn

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue
import java.util.function.BiConsumer

class LootTables(private val lookupProvider: HolderLookup.Provider) : LootTableSubProvider {
    companion object {
        private fun netherSpawnLootTable(name: String): ResourceKey<LootTable> {
            return ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(NetherSpawn.ID, name))
        }
        val NETHER_PORTAL_FRAME_DEGRADATION = netherSpawnLootTable("nether_portal_frame_degradation")
    }

    override fun generate(consumer: BiConsumer<ResourceKey<LootTable>, LootTable.Builder>) {
        consumer.accept(NETHER_PORTAL_FRAME_DEGRADATION, LootTable.lootTable()
            .withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1f))
                .add(LootItem.lootTableItem(Blocks.BLACKSTONE))
            )
        )
    }
}

