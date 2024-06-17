package supa.dupa.mysqltest.entities

import java.sql.Timestamp

sealed class Game {
    abstract val id : Long?
    abstract val timestamp : Timestamp
}

