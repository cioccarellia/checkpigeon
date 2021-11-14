package com.cioccarellia.checkpigeon.logic.model.move

sealed class MoveType {
    abstract val symbol: String
}

class Movement : MoveType() {
    override val symbol = "-"
}

class Capture : MoveType() {
    override val symbol = "x"
}