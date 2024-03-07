package supa.dupa.mysqltest.entities

import kotlinx.serialization.Serializable

@Serializable
data class PostResponse(
    val code : Int,
    val message : String = ""
)
