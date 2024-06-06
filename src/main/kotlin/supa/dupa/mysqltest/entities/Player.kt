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
