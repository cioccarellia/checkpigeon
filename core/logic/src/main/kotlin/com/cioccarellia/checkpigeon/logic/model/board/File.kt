package com.cioccarellia.checkpigeon.logic.model.board

/**
 * Represents a board rank. Starts at 8 through H.
 * */
@JvmInline
value class File(val letter: FileLetter) {
    companion object {
        fun from(numeric: Int) = File(FileLetter.from(numeric))
    }
}

enum class FileLetter(val numeric: Int) {
    A(1),
    B(2),
    C(3),
    D(4),
    E(5),
    F(6),
    G(7),
    H(8);

    companion object {
        fun from(numeric: Int): FileLetter = values()[numeric - 1]
    }
}