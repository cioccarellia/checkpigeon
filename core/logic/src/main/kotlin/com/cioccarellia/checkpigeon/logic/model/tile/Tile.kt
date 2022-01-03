package com.cioccarellia.checkpigeon.logic.model.tile

import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material

/**
 * Represents one of 64 tiles, with color, coordinates and material
 * */
class Tile(
    val coordinate: Coordinate,
    material: Material
) {
    var material = material
        set(value) {
            require((material != Material.Empty && coordinate.parity) || (material == Material.Empty))
            field = value
        }

    val color: TileColor
        get() = if (coordinate.parity) TileColor.BLACK else TileColor.WHITE
}