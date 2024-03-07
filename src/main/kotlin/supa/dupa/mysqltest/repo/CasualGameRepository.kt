package supa.dupa.mysqltest.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import supa.dupa.mysqltest.entities.CasualGame
import supa.dupa.mysqltest.entities.RankGame

interface CasualGameRepository : JpaRepository<CasualGame, Int> {
    @Query(
        value = "SELECT TOP 10 * FROM CasualGame WHERE player1Id = :playerId OR player2Id = :playerId",
        nativeQuery = true
    )
    fun findResent10Game(
        @Param("playerId") playerId : Int
    ) : List<CasualGame>
}
