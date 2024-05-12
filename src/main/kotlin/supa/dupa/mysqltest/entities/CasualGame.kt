package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
data class CasualGame(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id : Long? = null,

    var player1Id : Long,
    var player2Id : Long,

    var player1WinCount : Int = 0,
    var player2WinCount : Int = 0
)
