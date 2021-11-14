package com.cioccarellia.checkpigeon.logic.model.tile

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material

/**
 * Represents one of 64 tiles, with color, coordinates and material
 * */
data class Tile(
    val coordinate: Coordinate,
    val material: Material
) {
   val color: TileColor
        get() = if (coordinate.parity) TileColor.BLACK else TileColor.WHITE
}