package com.cioccarellia.checkpigeon.generator

import com.cioccarellia.checkpigeon.functions.enumPieces
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import kotlinx.coroutines.yield


fun gen_all(board: Board, playingColor: TileColor): List<Move> {
    val generated: MutableList<Move> = mutableListOf()

    for (material in board.enumPieces(playingColor)) {
        generated += gen_for(board, /*material.first,*/ material.second, playingColor)
    }

    return generated
}


fun gen_for(board: Board, coordinate: Coordinate, playingColor: TileColor): List<Move> {
    return gmovements(board, coordinate, playingColor) + gcaptures(board, coordinate, playingColor)
}




fun gmovements(board: Board, coordinate: Coordinate, playingColor: TileColor): List<Move> {
    val material = board[coordinate]

    if (material.color() != null) {
        assert(playingColor == material.color())
    }

    val generated: MutableList<Move> = mutableListOf()

    val directions = material.validDirections(playingColor)

    if (directions != null) {
        for (it in directions) {
            if (isMovementLegal(board, playingColor, coordinate, it)) {
                val endCoordinate = coordinate.apply(it, playingColor)!!

                generated += Move(
                    moveType = MoveType.Movement,
                    playingColor = playingColor,
                    start = coordinate,
                    end = endCoordinate,
                    captures = listOf(),
                    blows = null
                )
            }
        }
    }

    return generated
}







fun gcaptures(board: Board, /*material: Material,*/ coordinate: Coordinate, playingColor: TileColor): List<Move> {


    return listOf();
}