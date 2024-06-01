package supa.dupa.mysqltest.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import supa.dupa.mysqltest.entities.RankGame

interface RankGameRepository : JpaRepository<RankGame, Long> {
    @Query(
        value = "SELECT * FROM tb_rank_game WHERE player1_id = :id OR player2_id = :id ORDER BY id DESC LIMIT 10",
        nativeQuery = true
    )
    fun findResent10Game(
        @Param("id") id : Long
    ) : List<RankGame>

    @Query(
        value = "SELECT * FROM tb_rank_game WHERE player1_id = :id OR player2_id = :id ORDER BY id DESC",
        nativeQuery = true
    )
    fun findAllGame(
        @Param("id") id : Long
    ) : List<RankGame>
}
