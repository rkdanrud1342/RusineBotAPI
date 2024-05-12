package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Player(
    @Id
    var id : Long,
    var name : String,
    var grade : Int = 0,
    var casualWinCount : Int = 0,
    var casualLoseCount : Int = 0,
    var rankWinCount : Int = 0,
    var rankLoseCount : Int = 0,
    var afkCount : Int = 0,
    var eloScore : Double = 1000.0,
)