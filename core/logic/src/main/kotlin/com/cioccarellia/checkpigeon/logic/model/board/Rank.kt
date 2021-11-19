package com.cioccarellia.checkpigeon.logic.model.board

/**
 * Represents a board file. Starts at 1 through 8.
 * */
@JvmInline
value class Rank(val number: Int) {
    init {
        require(number in 1..8)
    }

    companion object {
        fun from(numeric: Int) = Rank(numeric)
    }
}