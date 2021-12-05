package com.cioccarellia.checkpigeoncli.executors.game.input

import com.cioccarellia.checkpigeon.logic.model.move.linear.Move

sealed class ParsedMove {
    data class Success(
        val move: Move
    ) : ParsedMove()

    data class Failure(
        val message: String
    ) : ParsedMove()
}