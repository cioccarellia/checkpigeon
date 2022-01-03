package com.cioccarellia.checkpigeon.logic.model.tile

enum class TileColor {
    WHITE,
    BLACK;

    override fun toString(): String {
        return super.toString().lowercase()
    }

    operator fun not() = when (this) {
        WHITE -> BLACK
        BLACK -> WHITE
    }
}