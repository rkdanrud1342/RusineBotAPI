package supa.dupa.mysqltest.entities

import jakarta.persistence.*

@Entity
data class RankGame(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,

    var gameType : String,

    var player1Id : Long,
    var player2Id : Long,

    var player1EstimateWinRate : Double? = null,
    var player2EstimateWinRate : Double? = null,

    var player1WinCount : Int = 0,
    var player2WinCount : Int = 0,
)
