package net.othrayte.netherspawn

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks.*
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import java.util.concurrent.CompletableFuture


object Tags {
    object Blocks {
        // Create a tag key for behaviour that is common to multiple mods
        private fun commonTag(name: String): TagKey<Block> {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath("c", name))
        }

        // Create a tag key for behaviour that is specific to the NetherSpawn mod
        private fun netherSpawnTag(name: String): TagKey<Block> {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(NetherSpawn.ID, name))
        }

        // Tag for blocks that can damage the player (not including suffocation)
        val DAMAGES_PLAYER = commonTag("damages_player")

        // Tag provider
        class Provider(
            output: PackOutput,
            lookupProvider: CompletableFuture<HolderLookup.Provider?>,
            existingFileHelper: ExistingFileHelper?
        ) : BlockTagsProvider(output, lookupProvider, NetherSpawn.ID, existingFileHelper) {

            // Generates our tags
            override fun addTags(lookupProvider: HolderLookup.Provider) {
                tag(DAMAGES_PLAYER).add(LAVA, CACTUS, MAGMA_BLOCK).addTag(BlockTags.FIRE)
            }
        }
    }
}