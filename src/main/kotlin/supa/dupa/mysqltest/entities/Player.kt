package supa.dupa.mysqltest.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table

@Entity(name = "tb_player")
@Table(indexes = [Index(name = "idx_elo_score", columnList = "elo_score")])
data class Player(
    @Id
    var id : Long,

    @Column(name = "name")
    var name : String,

    @Column(name = "elo_score")
    var eloScore : Double,
)

data class PlayerDTO(
    val id : Long,
    val name : String,
    val casualWinCount : Int,
    val casualLoseCount : Int,
    val rankWinCount : Int,
    val rankLoseCount : Int,
    val eloScore : Int
)
