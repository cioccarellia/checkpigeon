package com.cioccarellia.checkpigeon.logic.model.move

sealed class MoveType {
    object Movement : MoveType()
    object Capture : MoveType()
}

fun MoveType.symbol() = when(this) {
    is MoveType.Capture -> "x"
    is MoveType.Movement -> "-"
}