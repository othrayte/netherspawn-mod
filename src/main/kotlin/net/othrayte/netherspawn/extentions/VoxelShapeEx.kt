package net.othrayte.netherspawn.extentions

import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.VoxelShape

object VoxelShapeEx {
    // Check if the voxel shape intersects with the given box
    fun VoxelShape.intersects(box: AABB): Boolean {
        var intersects = false
        forAllBoxes({ minX, minY, minZ, maxX, maxY, maxZ ->
            if (box.intersects(AABB(minX, minY, minZ, maxX, maxY, maxZ))) {
                intersects = true
                return@forAllBoxes
            }
        })
        return intersects
    }
}