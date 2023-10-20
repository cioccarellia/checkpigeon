package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.generator.gen_all
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import org.junit.Test

class MoveGenTests {

    private val engine = Engine(
        Player("A/P", TileColor.WHITE, true) to Player("A/THR", TileColor.BLACK, false)
    )

    @Test
    fun gen_capture() {
        val board = engine.expose()
        val coord = Coordinate(File.Companion.from('D'), Rank(6))

        BoardPrinter.stdoutc(engine.expose(), TileColor.WHITE, listOf(coord))

        val list = gen_all(board, TileColor.BLACK)
        println(list.joinToString(separator = "\n"))

    }

}