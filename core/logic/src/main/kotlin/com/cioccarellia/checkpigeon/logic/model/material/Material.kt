package com.cioccarellia.checkpigeon.logic.model.material

import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

/**
 * Material is whatever can be put inside a tile
 * */
sealed class Material {

    class Dama(val color: TileColor) : Material()
    class Damone(val color: TileColor) : Material()
    object Empty : Material()
}
