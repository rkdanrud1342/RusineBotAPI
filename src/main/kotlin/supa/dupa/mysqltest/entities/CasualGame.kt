package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class CasualGame(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Int? = null,
    var player1Id : String,
    var player2Id : String,
    var player1WinCount : Int = 0,
    var player2WinCount : Int = 0
)
