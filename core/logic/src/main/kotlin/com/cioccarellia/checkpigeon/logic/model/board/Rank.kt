package com.cioccarellia.checkpigeon.logic.model.board

/**
 * Represents a board file. Starts at 1 through 8.
 * */
@JvmInline
value class Rank(val number: Int) {
    init {
        require(number in 1..8)
    }

    fun complement() = when (number) {
        1 -> Rank(8)
        2 -> Rank(7)
        3 -> Rank(6)
        4 -> Rank(5)
        5 -> Rank(4)
        6 -> Rank(3)
        7 -> Rank(2)
        8 -> Rank(1)
        else -> throw IllegalArgumentException("Numeric not in range")
    }

    companion object {
        fun from(numeric: Int) = Rank(numeric)
    }
}