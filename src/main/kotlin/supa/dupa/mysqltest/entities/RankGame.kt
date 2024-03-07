package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class RankGame(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Int? = null,
    var gameType : String,
    var player1Id : ULong,
    var player2Id : ULong,
    var player1EstimateWinRate : Double? = null,
    var player2EstimateWinRate : Double? = null,
    var player1WinCount : Int = 0,
    var player2WinCount : Int = 0,
)
