package com.vilocmaker.viloc.model

data class Nodes(
    val name: String,
    var id: String? = null,
    var limit: Int? = null,
    var offset: Int? = null
    ) {}