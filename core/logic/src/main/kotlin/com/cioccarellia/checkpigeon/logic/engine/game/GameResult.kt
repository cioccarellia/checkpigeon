package com.cioccarellia.checkpigeon.logic.engine.game

import com.cioccarellia.checkpigeon.logic.model.player.Player

sealed class GameResult {
    object Draw : GameResult()
    class Done(
        val winner: Player,
        val loser: Player
    ) : GameResult()
}