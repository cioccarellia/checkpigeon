package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.generator.gen_all
import com.cioccarellia.checkpigeon.generator.gen_for
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import org.junit.Test

class MatrixTests {

    private val engine = Engine(
        Player("W0", TileColor.WHITE, true) to Player("B1", TileColor.BLACK, false)
    )

    @Test
    fun x() {
        val board = engine.expose()
        val coord = Coordinate(File.Companion.from('D'), Rank(6))

        BoardPrinter.stdoutc(engine.expose(), TileColor.WHITE, listOf(coord))

        val list = gen_all(board, TileColor.WHITE)
        println(list.joinToString(separator = "\n"))

    }

}