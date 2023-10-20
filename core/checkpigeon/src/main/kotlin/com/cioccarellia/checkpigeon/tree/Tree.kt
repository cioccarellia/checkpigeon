package com.cioccarellia.checkpigeon.tree

import com.cioccarellia.checkpigeon.model.State

data class Tree(
    val parent: Tree? = null,
    val left: Tree? = null,
    val right: Tree? = null,

    val key: Int,
    val state: State
)