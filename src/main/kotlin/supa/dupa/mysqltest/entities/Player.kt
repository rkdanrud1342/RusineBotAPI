package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class Player(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : String? = null,
    var name : String,
    var grade : Int = 0,
    var winCount : Int = 0,
    var loseCount : Int = 0,
    var afkCount : Int = 0,
    var eloScore : Double = 1000.0,
)
