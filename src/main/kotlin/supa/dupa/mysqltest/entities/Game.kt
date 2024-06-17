package supa.dupa.mysqltest.entities

import java.sql.Timestamp

sealed class Game {
    abstract val id : Long?
    abstract val player1Id : Long
    abstract val player2Id : Long
    abstract var player1WinCount : Int
    abstract var player2WinCount : Int
    abstract val timestamp : Timestamp
}
