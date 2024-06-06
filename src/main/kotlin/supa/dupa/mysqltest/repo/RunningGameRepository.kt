package supa.dupa.mysqltest.repo

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import supa.dupa.mysqltest.entities.RunningGame

interface RunningGameRepository : JpaRepository<RunningGame, Long> {
    @Query(
        value = "SELECT * FROM tb_running_game WHERE player1_id = :id OR player2_id = :id LIMIT 1",
        nativeQuery = true
    )
    fun findByPlayerId(
        @Param("id") id : Long
    ) : RunningGame?
}