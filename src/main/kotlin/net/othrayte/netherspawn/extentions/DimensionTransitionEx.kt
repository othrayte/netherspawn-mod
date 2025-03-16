package net.othrayte.netherspawn.extentions

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3

object DimensionTransitionEx {
    // Create a new dimension transition with the given location
    fun DimensionTransition.withLocation(level: ServerLevel, pos: Vec3): DimensionTransition {
        return DimensionTransition(level, pos, speed, yRot, xRot, missingRespawnBlock, postDimensionTransition)
    }
}
