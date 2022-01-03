package com.cioccarellia.checkpigeon.logic.model.material

import com.cioccarellia.checkpigeon.constants.*
import com.cioccarellia.checkpigeon.constants.BLACK_DAMA
import com.cioccarellia.checkpigeon.constants.BLACK_DAMONE
import com.cioccarellia.checkpigeon.constants.WHITE_DAMA
import com.cioccarellia.checkpigeon.constants.WHITE_DAMONE
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Material is whatever can be put inside a tile
 * */
sealed class Material {
    abstract fun promoted(): Material
    abstract fun demoted(): Material

    data class Dama(val color: TileColor) : Material() {
        override fun promoted() = Damone(color)
        override fun demoted() = throw IllegalStateException()

        override fun toString() = if (color == TileColor.BLACK) BLACK_DAMA else WHITE_DAMA
    }

    data class Damone(val color: TileColor) : Material() {
        override fun promoted() = throw IllegalStateException()
        override fun demoted() = Dama(color)

        override fun toString() = if (color == TileColor.BLACK) BLACK_DAMONE else WHITE_DAMONE
    }

    object Empty : Material() {
        override fun promoted() = throw IllegalStateException()
        override fun demoted() = throw IllegalStateException()

        override fun toString() = EMPTY_SQUARE
    }
}
