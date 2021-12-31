package com.cioccarellia.checkpigeon.logic.model.tile

enum class TileColor {
    BLACK,
    WHITE;

    override fun toString(): String {
        return super.toString().lowercase()
    }

    operator fun not() = when (this) {
        BLACK -> WHITE
        WHITE -> BLACK
    }
}