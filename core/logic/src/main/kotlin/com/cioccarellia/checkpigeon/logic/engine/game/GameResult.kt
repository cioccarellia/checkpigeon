package com.cioccarellia.checkpigeon.logic.engine.game

import com.cioccarellia.checkpigeon.logic.engine.status.EngineMoveReceiver
import com.cioccarellia.checkpigeon.logic.engine.verifier.RejectionReason
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

sealed class GameResult {
    object Draw : GameResult()
    class Done(
        val winner: Player,
        val loser: Player
    ) : GameResult()
}