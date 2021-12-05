package com.cioccarellia.checkpigeon.logic.model.move

sealed class MoveType {
    object Movement : MoveType() {
        override fun toString() = "Movement"
    }

    object Capture : MoveType() {
        override fun toString() = "Capture"
    }
}

fun MoveType.symbol() = when (this) {
    is MoveType.Capture -> "x"
    is MoveType.Movement -> "-"
}