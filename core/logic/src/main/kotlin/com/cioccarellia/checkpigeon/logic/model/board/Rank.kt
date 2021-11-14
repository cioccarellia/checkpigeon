package com.cioccarellia.checkpigeon.logic.model.board

/**
 * Represents a board file. Starts at 1 through 8.
 * */
@JvmInline
value class Rank(val value: Int) {
    init {
        require(value in 1..8)
    }
}