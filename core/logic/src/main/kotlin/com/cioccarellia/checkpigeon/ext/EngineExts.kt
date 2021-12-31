package com.cioccarellia.checkpigeon.ext

import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

fun Pair<Player, Player>.findPlayerWithColor(color: TileColor) = when (color) {
    first.color -> {
        first
    }
    second.color -> {
        second
    }
    else -> throw IllegalArgumentException("Data inconsistency in $this: no player colored $color")
}