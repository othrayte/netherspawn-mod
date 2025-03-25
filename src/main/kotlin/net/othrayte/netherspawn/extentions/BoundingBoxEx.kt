package net.othrayte.netherspawn.extentions

import net.minecraft.core.BlockPos
import net.minecraft.world.level.levelgen.structure.BoundingBox

object BoundingBoxEx {
    // Sequence of all positions in the bounding box (in unspecified order)
    fun BoundingBox.positions(): Sequence<BlockPos> {
        return sequence {
            for (x in minX()..maxX()) {
                for (y in minY()..maxY()) {
                    for (z in minZ()..maxZ()) {
                        yield(BlockPos(x, y, z))
                    }
                }
            }
        }
    }

    // Sequence of all positions in the bounding box, ordered generally by distance from the center
    fun BoundingBox.positionsByDistanceFromCenter(): Sequence<BlockPos> {
        val center = center
        val maxRadius = maxOf(xSpan, ySpan, zSpan) / 2
        return sequence {
            var radius = 0
            while (radius <= maxRadius) {
                val minX = center.x - radius
                val minY = center.y - radius
                val minZ = center.z - radius
                val maxX = center.x + radius
                val maxY = center.y + radius
                val maxZ = center.z + radius
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        for (z in minZ..maxZ) {
                            val pos = BlockPos(x, y, z)
                            if (isInside(pos)) {
                                yield(pos)
                            }
                        }
                    }
                }
                radius++
            }
        }
    }

    // Sequence of all positions in the bounding box, ordered by layers starting at the middle, then going down, then
    // up. In each layer, the positions are ordered top to bottom then generally by distance from the center.
    fun BoundingBox.centerOutInLayersDownUp(layerHeight: Int = 4): Sequence<BlockPos> {
        val center = center
        val maxHRadius = maxOf(xSpan, zSpan) / 2
        // Make sequence of layers, starting from one centered in the middle of y and then the lower ones, then the upper ones
        val layers = sequence {
            val centerLayerTop = center.y + (layerHeight / 2)
            this.yield(centerLayerTop downTo centerLayerTop - layerHeight + 1)
            for (y in centerLayerTop - layerHeight downTo minY() + 1 step layerHeight) {
                this.yield(y downTo maxOf(y - layerHeight, minY()) + 1)
            }
            for (y in centerLayerTop..maxY() step layerHeight) {
                this.yield(minOf(y + layerHeight, maxY()) downTo y + 1)
            }
        }
        return sequence {
            for (layer in layers) {
                var radius = 0
                while (radius <= maxHRadius) {
                    val minX = center.x - radius
                    val minZ = center.z - radius
                    val maxX = center.x + radius
                    val maxZ = center.z + radius
                    for (x in minX..maxX) {
                        for (z in minZ..maxZ) {
                            for (y in layer) {
                                val pos = BlockPos(x, y, z)
                                if (isInside(pos)) {
                                    yield(pos)
                                }
                            }
                        }
                    }
                    radius++
                }
            }
        }
    }

    // Expand the bounding box by the given amount in all directions
    fun BoundingBox.expandBy(amount: Int): BoundingBox {
        return BoundingBox(minX() - amount, minY() - amount, minZ() - amount, maxX() + amount, maxY() + amount, maxZ() + amount)
    }

    // Limit the bounding box to the given range in the Y direction
    fun BoundingBox.limitY(minY: Int, maxY: Int): BoundingBox {
        return BoundingBox(minX(), minY().coerceAtLeast(minY), minZ(), maxX(), maxY().coerceAtMost(maxY), maxZ())
    }
}