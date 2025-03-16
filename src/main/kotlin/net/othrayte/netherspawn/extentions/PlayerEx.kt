package net.othrayte.netherspawn.extentions

import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stats

object PlayerEx {
    // Check if this is the first time the player has logged in
    fun ServerPlayer.isFirstLogin(): Boolean {
        return stats.getValue(Stats.CUSTOM.get(Stats.LEAVE_GAME)) == 0
    }
}