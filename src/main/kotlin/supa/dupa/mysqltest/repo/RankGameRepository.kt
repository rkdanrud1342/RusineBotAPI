package supa.dupa.mysqltest.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import supa.dupa.mysqltest.entities.RankGame

interface RankGameRepository : JpaRepository<RankGame, Long> {
    @Query(
        value = "SELECT TOP 10 * FROM RankGame WHERE player1Id = :playerId OR player2Id = :playerId ORDER BY id DESC",
        nativeQuery = true
    )
    fun findResent10Game(
        @Param("playerId") playerId : Long
    ) : List<RankGame>
}
