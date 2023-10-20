package com.cioccarellia.checkpigeon.generator

import com.cioccarellia.checkpigeon.functions.copyAndApplyMove
import com.cioccarellia.checkpigeon.functions.enumPieces
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import java.util.LinkedList


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
    val generated: MutableList<Move> = mutableListOf()

    // extract material
    val material = board[coordinate]

    if (material.color() != null) {
        assert(playingColor == material.color())
    }

    // get all viable directions
    val directions = material.validDirections(playingColor)


    // for each direction, check
    if (directions != null) {
        for (it in directions) {
            if (isMovementLegal(board, playingColor, coordinate, it)) {
                val endCoordinate = coordinate.apply(it, playingColor, amount = 1)!!

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


fun gcaptures(board: Board, coordinate: Coordinate, playingColor: TileColor): List<Move> {
    val startingMaterial = board[coordinate]

    if (startingMaterial is Material.Empty) {
        return listOf()
    }

    return rec_explore_paths(
        board = board,
        startingMaterial = startingMaterial,
        startingCoordinate = coordinate,
        playingColor = playingColor,
        recursiveCoordinate = coordinate,
        visitedCoordinates = mutableSetOf(),
        captures = mutableListOf()
    )
}


fun rec_explore_paths(
    board: Board,
    startingMaterial: Material,
    startingCoordinate: Coordinate,
    playingColor: TileColor,
    recursiveCoordinate: Coordinate,
    visitedCoordinates: MutableSet<Coordinate>,
    captures: MutableList<Coordinate>
): List<Move> {
    val generated: MutableList<Move> = mutableListOf()

    val directions = startingMaterial.validDirections(playingColor)!!

    // get all viable directions
    for (direction in directions) {
        val capturedCoordinate = recursiveCoordinate.apply(direction, playingColor, amount = 1)
        val endCoordinate = recursiveCoordinate.apply(direction, playingColor, amount = 2)

        if (endCoordinate != null && visitedCoordinates.contains(endCoordinate)) {
            continue
        }

        if (canCaptureAndJumpOver(board, playingColor, recursiveCoordinate, direction) && capturedCoordinate != null && endCoordinate != null) {
            // add to list
            visitedCoordinates.add(recursiveCoordinate)
            captures.add(capturedCoordinate)

            // generate move
            val generatedMove = Move(
                moveType = MoveType.Capture,
                playingColor = playingColor,
                start = startingCoordinate,
                end = endCoordinate,
                captures = LinkedList(captures),
                blows = null
            )

            generated += generatedMove

            // recursive step
            generated += rec_explore_paths(board.copyAndApplyMove(generatedMove), startingMaterial, startingCoordinate, playingColor, endCoordinate, visitedCoordinates, captures)

            // remove from lists
            visitedCoordinates.remove(recursiveCoordinate)
            captures.remove(capturedCoordinate)
        }
    }

    return generated
}





