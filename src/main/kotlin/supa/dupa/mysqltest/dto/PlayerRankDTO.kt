package supa.dupa.mysqltest.dto

import supa.dupa.mysqltest.entities.Player

data class PlayerRankDTO(
    val top10 : List<Player>,
    val player : Player,
    val rank : Int
)