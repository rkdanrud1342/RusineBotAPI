package supa.dupa.mysqltest.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import supa.dupa.mysqltest.entities.CasualGame

interface CasualGameRepository : JpaRepository<CasualGame, Long> {
    @Query(
        value = "SELECT * FROM tb_casual_game g WHERE g.player1_id = :id OR g.player2_id = :id ORDER BY id DESC LIMIT :amount",
        nativeQuery = true
    ) fun findResentGame(
        @Param("id") id : Long,
        @Param("amount") amount : Int
    ) : List<CasualGame>

    @Query(
        value = "SELECT * FROM tb_casual_game WHERE player1_id = :id OR player2_id = :id ORDER BY id DESC",
        nativeQuery = true
    )
    fun findAllGame(
        @Param("id") id : Long
    ) : List<CasualGame>
}
