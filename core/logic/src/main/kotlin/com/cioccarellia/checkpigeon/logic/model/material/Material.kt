package com.cioccarellia.checkpigeon.logic.model.material

import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Material is whatever can be put inside a tile
 * */
sealed class Material {
    data class Dama(val color: TileColor) : Material() {
        override fun toString() = if (color == TileColor.BLACK) BLACK_DAMA else WHITE_DAMA
    }

    data class Damone(val color: TileColor) : Material() {
        override fun toString() = if (color == TileColor.BLACK) BLACK_DAMONE else WHITE_DAMONE
    }

    object Empty : Material() {
        override fun toString() = EMPTY_SQUARE
    }

    internal val BLACK_DAMA = "○"
    internal val WHITE_DAMA = "●"

    internal val BLACK_DAMONE = "Δ"
    internal val WHITE_DAMONE = "▲"

    internal val EMPTY_SQUARE = " "
}
