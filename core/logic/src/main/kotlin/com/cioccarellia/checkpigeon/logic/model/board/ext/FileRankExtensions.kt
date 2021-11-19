package com.cioccarellia.checkpigeon.logic.model.board.ext

import com.cioccarellia.checkpigeon.logic.model.board.File
import com.cioccarellia.checkpigeon.logic.model.board.Rank

/**
 * Numeric sum for parity check
 * */
operator fun Rank.plus(file: File): Int = this.number + file.letter.numeric

/**
 * Numeric sum for parity check
 * */
operator fun File.plus(rank: Rank): Int = this.letter.numeric + rank.number