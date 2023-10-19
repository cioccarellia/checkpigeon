package com.cioccarellia.checkpigeon.logic

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.engine.internal.BoardPrinter
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import org.junit.Test
import kotlin.test.assertEquals

class MatrixTests {

    private val board = Board()

    @Test
    fun checkBorderCoords() {
        BoardPrinter.stdout(board, TileColor.WHITE, listOf(Coordinate.pair(1,1), Coordinate.pair(1,8), Coordinate.pair(8,1),  Coordinate.pair(8,8)))

        // A file extremes
        val a1 = board.matrix[0][0]
        assertEquals(Coordinate.raw(1,1), a1.coordinate)
        assertEquals(Material.Dama(TileColor.WHITE), a1.material)


        val a8 = board.matrix[0][7]
        assertEquals(Coordinate.raw(1,8), a8.coordinate)
        assertEquals(Material.Empty, a8.material)


        // H file extremes
        val h1 = board.matrix[7][0]
        assertEquals(Coordinate.raw(8,1), h1.coordinate)
        assertEquals(Material.Empty, h1.material)


        val h8 = board.matrix[7][7]
        assertEquals(Coordinate.raw(8,8), h8.coordinate)
        assertEquals(Material.Dama(TileColor.BLACK), h8.material)
    }


    @Test
    fun checkBoardCenterCoords() {
        // D file center
        val d4 = board.matrix[3][3]
        assertEquals(Coordinate.raw(4,4), d4.coordinate)

        val d5 = board.matrix[3][4]
        assertEquals(Coordinate.raw(4,5), d5.coordinate)


        // E file extremes
        val e4 = board.matrix[4][3]
        assertEquals(Coordinate.raw(5,4), e4.coordinate)

        val e5 = board.matrix[4][4]
        assertEquals(Coordinate.raw(5,5), e5.coordinate)
    }

    @Test
    fun checkBlackPieces() {
        val whiteDama = Material.Dama(TileColor.WHITE)

        // A1 white dama
        assertEquals(
            whiteDama,
            board.matrix[0][0].material
        )

        // A3 white dama
        assertEquals(
            whiteDama,
            board.matrix[0][2].material
        )

        // E3 white dama
        assertEquals(
            whiteDama,
            board.matrix[4][2].material
        )

        // H2 white dama
        assertEquals(
            whiteDama,
            board.matrix[7][1].material
        )
    }

    @Test
    fun checkWhitePieces() {
        val blackDama = Material.Dama(TileColor.BLACK)

        // H8 black dama
        assertEquals(
            blackDama,
            board.matrix[7][7].material
        )

        // D6 black dama
        assertEquals(
            blackDama,
            board.matrix[3][5].material
        )

        // B6 black dama
        assertEquals(
            blackDama,
            board.matrix[1][5].material
        )
    }


    @Test
    fun checkEmptySquares() {
        val empty = Material.Empty

        // H1 empty
        assertEquals(
            empty,
            board.matrix[7][0].material
        )

        // A2 empty
        assertEquals(
            empty,
            board.matrix[0][1].material
        )

        // B1 empty
        assertEquals(
            empty,
            board.matrix[1][0].material
        )

        // D4 empty
        assertEquals(
            empty,
            board.matrix[3][3].material
        )

        // C5 empty
        assertEquals(
            empty,
            board.matrix[4][4].material
        )
    }
}