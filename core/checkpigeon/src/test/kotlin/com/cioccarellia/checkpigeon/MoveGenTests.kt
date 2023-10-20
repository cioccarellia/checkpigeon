package com.cioccarellia.checkpigeon

import com.cioccarellia.checkpigeon.generator.gen_all
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.Engine
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.player.Player
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import org.junit.Test

class MoveGenTests {

    private val board = Board(applyInit = false)

    @Test
    fun gen_capture() {
        board[Coordinate.raw(1,1)] = Material.Dama(TileColor.WHITE)
        board[Coordinate.raw(2,2)] = Material.Dama(TileColor.BLACK)
        board[Coordinate.raw(4,4)] = Material.Dama(TileColor.BLACK)
        board[Coordinate.raw(2,4)] = Material.Dama(TileColor.BLACK)

        BoardPrinter.stdout(board, TileColor.WHITE)

        val list = gen_all(board, TileColor.WHITE)
        println(list.joinToString(separator = "\n"))

    }

}