package supa.dupa.mysqltest.entities

import jakarta.persistence.*

@Entity(name = "tb_rank_game")
@Table(indexes = [Index(columnList = "player1_id"), Index(columnList = "player2_id")])
data class RankGame(
    @Id
    val id : Long? = null,

    @Column(name = "player1_id")
    val player1Id : Long,
    @Column(name = "player2_id")
    val player2Id : Long,

    @Column(name = "player1_estimate_win_rate")
    val player1EstimateWinRate : Double,
    @Column(name = "player2_estimate_win_rate")
    val player2EstimateWinRate : Double,

    @Column(name = "player1_win_count")
    var player1WinCount : Int = 0,
    @Column(name = "player2_win_count")
    var player2WinCount : Int = 0,
)
