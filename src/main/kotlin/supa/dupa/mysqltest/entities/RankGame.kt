package supa.dupa.mysqltest.entities

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import java.sql.Timestamp
import java.time.OffsetDateTime
import java.time.ZoneId

@Entity(name = "tb_rank_game")
@Table(indexes = [Index(columnList = "player1_id"), Index(columnList = "player2_id")])
data class RankGame(
    @Id
    override val id : Long? = null,

    @Column(name = "player1_id", nullable = false)
    override val player1Id : Long,
    @Column(name = "player2_id", nullable = false)
    override val player2Id : Long,

    @Column(name = "player1_estimate_win_rate", nullable = false)
    val player1EstimateWinRate : Double,
    @Column(name = "player2_estimate_win_rate", nullable = false)
    val player2EstimateWinRate : Double,

    @Column(name = "player1_win_count", nullable = false)
    override var player1WinCount : Int = 0,
    @Column(name = "player2_win_count", nullable = false)
    override var player2WinCount : Int = 0,

    @Column(name = "reg_datetime", nullable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP(6)")
    override val timestamp : Timestamp = Timestamp.valueOf(OffsetDateTime.now(ZoneId.systemDefault()).toLocalDateTime())
) : Game()
