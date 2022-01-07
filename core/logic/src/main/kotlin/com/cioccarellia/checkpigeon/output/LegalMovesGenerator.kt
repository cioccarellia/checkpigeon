package com.cioccarellia.checkpigeon.output

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.board.Direction
import com.cioccarellia.checkpigeon.logic.board.areCoordinatesCompatibleForMovement
import com.cioccarellia.checkpigeon.logic.board.isPromotionSquare
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

typealias MC = Pair<Material, Coordinate>

object LegalMovesGenerator {
    fun generate(
        board: Board,
        mc: MC
    ): List<Move> = movements(mc) + captures(mc)

    private inline fun movements(mc: MC): List<Move> = when (val material = mc.first) {
        is Material.Dama -> {
            val partialMoves = mutableListOf<Move>()
            val start = mc.second
            val color = material.color

            if (color == TileColor.WHITE) {
                val landingNWSquare = Direction.NW.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingNWSquare, Direction.NW)) {
                    partialMoves.add(
                        Move(
                            moveType = MoveType.Movement,
                            playingColor = TileColor.WHITE,
                            start = start,
                            landingNWSquare,
                            listOf(),
                            null
                        ).apply {
                            if (isPromotionSquare(color = color, square = landingNWSquare)) {
                                promotion = landingNWSquare
                            }
                        }
                    )
                }

                val landingNESquare = Direction.NE.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingNESquare, Direction.NE)) {
                    partialMoves.add(
                        Move(
                            moveType = MoveType.Movement,
                            playingColor = TileColor.WHITE,
                            start = start,
                            landingNWSquare,
                            listOf(),
                            null
                        ).apply {
                            if (isPromotionSquare(color = color, square = landingNESquare)) {
                                promotion = landingNESquare
                            }
                        }
                    )
                }
            } else {
                val landingSWSquare = Direction.SW.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingSWSquare, Direction.SW)) {
                    partialMoves.add(
                        Move(
                            moveType = MoveType.Movement,
                            playingColor = TileColor.BLACK,
                            start = start,
                            landingSWSquare,
                            listOf(),
                            null
                        ).apply {
                            if (isPromotionSquare(color = color, square = landingSWSquare)) {
                                promotion = landingSWSquare
                            }
                        }
                    )
                }

                val landingSESquare = Direction.SE.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingSESquare, Direction.SE)) {
                    partialMoves.add(
                        Move(
                            moveType = MoveType.Movement,
                            playingColor = TileColor.BLACK,
                            start = start,
                            landingSESquare,
                            listOf(),
                            null
                        ).apply {
                            if (isPromotionSquare(color = color, square = landingSESquare)) {
                                promotion = landingSESquare
                            }
                        }
                    )
                }
            }

            partialMoves
        }
        is Material.Damone -> {
            val partialMoves = mutableListOf<Move>()
            val start = mc.second
            val color = material.color

            val landingNWSquare = Direction.NW.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingNWSquare, Direction.NW)) {
                partialMoves.add(
                    Move(
                        moveType = MoveType.Movement,
                        playingColor = TileColor.WHITE,
                        start = start,
                        landingNWSquare,
                        listOf(),
                        null
                    ).apply {
                        if (isPromotionSquare(color = color, square = landingNWSquare)) {
                            promotion = landingNWSquare
                        }
                    }
                )
            }

            val landingNESquare = Direction.NE.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingNESquare, Direction.NE)) {
                partialMoves.add(
                    Move(
                        moveType = MoveType.Movement,
                        playingColor = TileColor.WHITE,
                        start = start,
                        landingNWSquare,
                        listOf(),
                        null
                    ).apply {
                        if (isPromotionSquare(color = color, square = landingNESquare)) {
                            promotion = landingNESquare
                        }
                    }
                )
            }
            val landingSWSquare = Direction.SW.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingSWSquare, Direction.SW)) {
                partialMoves.add(
                    Move(
                        moveType = MoveType.Movement,
                        playingColor = TileColor.BLACK,
                        start = start,
                        landingSWSquare,
                        listOf(),
                        null
                    ).apply {
                        if (isPromotionSquare(color = color, square = landingSWSquare)) {
                            promotion = landingSWSquare
                        }
                    }
                )
            }

            val landingSESquare = Direction.SE.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingSESquare, Direction.SE)) {
                partialMoves.add(
                    Move(
                        moveType = MoveType.Movement,
                        playingColor = TileColor.BLACK,
                        start = start,
                        landingSESquare,
                        listOf(),
                        null
                    ).apply {
                        if (isPromotionSquare(color = color, square = landingSESquare)) {
                            promotion = landingSESquare
                        }
                    }
                )
            }

            partialMoves
        }
        Material.Empty -> throw IllegalStateException()
    }


    private inline fun captures(mc: MC): List<Move> = when (val material = mc.first) {
        is Material.Dama -> {
            val partialMoves = mutableListOf<Move>()


            partialMoves
        }
        is Material.Damone -> {
            val partialMoves = mutableListOf<Move>()

            partialMoves
        }
        Material.Empty -> throw IllegalStateException()
    }

    private fun generateCapture

}