package com.cioccarellia.checkpigeon.logic.model.board

/**
 * Represents a board rank. Starts at 8 through H.
 * */
@JvmInline
value class File(val letter: FileLetter) {
    companion object {
        fun from(numeric: Int) = File(FileLetter.from(numeric))
        fun from(char: Char) = File(FileLetter.from(char))
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
        fun from(char: Char): FileLetter = when(char.lowercaseChar()) {
            'a' -> A
            'b' -> B
            'c' -> C
            'd' -> D
            'e' -> E
            'f' -> F
            'g' -> G
            'h' -> H
            else -> throw IllegalArgumentException("Given char $char to lowercase is not in range [a..h]")
        }
    }
}