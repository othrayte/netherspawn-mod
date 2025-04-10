package net.othrayte.netherspawn

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(NetherSpawn.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = NetherSpawn.ID)
object NetherSpawn {
    const val ID = "netherspawn"
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        ModLoadingContext.get().activeContainer.registerConfig(ModConfig.Type.SERVER, Options.CONFIG_SPEC)
    }

    // Setup datagen
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        event.generator.addProvider(
            event.includeServer(),
            Tags.Blocks.Provider(event.generator.packOutput, event.lookupProvider, event.existingFileHelper)
        )

        event.generator.addProvider(event.includeServer(),
            LootTableProvider(
                event.generator.packOutput, setOf(),
                listOf(LootTableProvider.SubProviderEntry(
                    { lookupProvider -> LootTables(lookupProvider) },
                    LootContextParamSets.EMPTY
                )),
                event.lookupProvider)
        )
    }
}
