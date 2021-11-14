package com.cioccarellia.checkpigeon.logic.model.material

/**
 * Material is whatever can be put inside a tile
 * */
sealed class Material

sealed class Dama : Material()
sealed class Damone : Material()
sealed class Empty : Material()