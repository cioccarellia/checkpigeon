package com.cioccarellia.checkpigeon.search

import com.cioccarellia.checkpigeon.logic.console.blue
import com.cioccarellia.checkpigeon.logic.console.green
import com.cioccarellia.checkpigeon.logic.console.red
import com.cioccarellia.checkpigeon.logic.console.yellow
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor


fun Int.grad(inverse: Boolean = false): String = when {
    this == 0 -> toString().yellow()
    this > 0 -> if (inverse) ("+" + toString()).red() else ("+" + toString()).green()
    this < 0 -> if (inverse) toString().green() else toString().red()
    else -> toString().blue()
}


fun dbg_tree(depth: Int, eval: Int, color: TileColor, move: Move?) {

    repeat(depth) {
        print("      ")
    }

    val out = buildString {
        append("[$depth]")
        append("[${color.toStringShort()}] ")
        append(eval.grad())
        append(" ${move?.humanMoveNotation()}")
    }

    println(out)
    System.out.flush()
}
