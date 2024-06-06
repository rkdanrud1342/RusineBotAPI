package supa.dupa.mysqltest.dto

data class PlayerDTO(
    val id : Long,
    val name : String,
    val casualWinCount : Int,
    val casualLoseCount : Int,
    val rankWinCount : Int,
    val rankLoseCount : Int,
    val eloScore : Int
)
