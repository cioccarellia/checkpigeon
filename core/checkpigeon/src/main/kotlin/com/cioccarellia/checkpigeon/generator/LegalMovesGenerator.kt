package com.cioccarellia.checkpigeon.generator

import com.cioccarellia.checkpigeon.logic.board.Board
import com.cioccarellia.checkpigeon.logic.board.CardinalDirection
import com.cioccarellia.checkpigeon.logic.board.areCoordinatesCompatibleForMovement
import com.cioccarellia.checkpigeon.logic.board.isPromotionSquare
import com.cioccarellia.checkpigeon.logic.model.board.Coordinate
import com.cioccarellia.checkpigeon.logic.model.material.Material
import com.cioccarellia.checkpigeon.logic.model.move.MoveType
import com.cioccarellia.checkpigeon.logic.model.move.linear.Move
import com.cioccarellia.checkpigeon.logic.model.tile.TileColor

object LegalMovesGenerator {
    fun generate(
        board: Board,
        mc: Pair<Material, Coordinate>
    ): List<Move> = movements(mc) + captures(mc)

    private fun movements(mc: Pair<Material, Coordinate>): List<Move> = when (val material = mc.first) {
        is Material.Dama -> {
            val partialMoves = mutableListOf<Move>()
            val start = mc.second
            val color = material.color

            if (color == TileColor.WHITE) {
                val landingNWSquare = CardinalDirection.NW.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingNWSquare, CardinalDirection.NW)) {
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

                val landingNESquare = CardinalDirection.NE.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingNESquare, CardinalDirection.NE)) {
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
                val landingSWSquare = CardinalDirection.SW.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingSWSquare, CardinalDirection.SW)) {
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

                val landingSESquare = CardinalDirection.SE.shiftedCoordinateBy1Diagonally(start)
                if (areCoordinatesCompatibleForMovement(start, landingSESquare, CardinalDirection.SE)) {
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

            val landingNWSquare = CardinalDirection.NW.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingNWSquare, CardinalDirection.NW)) {
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

            val landingNESquare = CardinalDirection.NE.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingNESquare, CardinalDirection.NE)) {
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
            val landingSWSquare = CardinalDirection.SW.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingSWSquare, CardinalDirection.SW)) {
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

            val landingSESquare = CardinalDirection.SE.shiftedCoordinateBy1Diagonally(start)
            if (areCoordinatesCompatibleForMovement(start, landingSESquare, CardinalDirection.SE)) {
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


    private fun captures(mc: Pair<Material, Coordinate>): List<Move> = when (val material = mc.first) {
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

}