package net.othrayte.netherspawn

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Mod(NetherSpawn.ID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = NetherSpawn.ID)
object NetherSpawn {
    const val ID = "netherspawn"
    val LOGGER: Logger = LogManager.getLogger(ID)

    // Setup datagen
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        event.generator.addProvider(
            event.includeServer(),
            Tags.Blocks.Provider(event.generator.packOutput, event.lookupProvider, event.existingFileHelper)
        )
    }
}
