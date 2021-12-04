package com.cioccarellia.checkpigeoncli.utils

import com.cioccarellia.checkpigeon.logic.console.*

fun String.toRandomColor() = when ((0..11).random()) {
    0 -> cyan()
    1 -> magenta()
    2 -> red()
    3 -> green()
    4 -> yellow()
    5 -> lightGreen()
    6 -> lightGray()
    7 -> lightRed()
    8 -> lightYellow()
    9 -> lightBlue()
    10 -> lightMagenta()
    11 -> lightCyan()
    else -> this
}