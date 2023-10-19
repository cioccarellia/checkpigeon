package com.cioccarellia.checkpigeon.functions

import com.cioccarellia.checkpigeon.MoveGenerator
import com.cioccarellia.checkpigeon.generator.LegalMovesGenerator
import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor
import com.cioccarellia.checkpigeon.model.Status


fun ToMove(status: Status): TileColor {
    return status.playerColor
}




fun IsTerminal(board: Board): Boolean {
    val whiteMaterial = board.countPieces(TileColor.WHITE);
    val blackMaterial = board.countPieces(TileColor.BLACK);

    if (whiteMaterial == 0 || blackMaterial == 0) {
        // I captured all my opponent material
        return true
    }

    // todo check stall
    return false;
}


fun IsTerminal(status: Status): Boolean {
    return IsTerminal(status.board)
}




fun Result(status: Status, move: Move): Status {
    assert(!status.isGameOver)

    val newBoard = status.board.copyAndApplyMove(move)

    return Status(
        board = newBoard,
        playerColor = status.playerColor.not(),
        isGameOver = IsTerminal(newBoard)
    )
}




fun Actions(status: Status): List<Move> {
    return LegalMovesGenerator.generate(status.board)
}




