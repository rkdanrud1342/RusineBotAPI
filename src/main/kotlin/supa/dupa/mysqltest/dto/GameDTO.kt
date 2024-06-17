package supa.dupa.mysqltest.dto

import supa.dupa.mysqltest.entities.GameType
import supa.dupa.mysqltest.entities.Player
import java.time.LocalDateTime

sealed interface GameDTO

data class GameResultDTO(
    val gameType : GameType,

    val player1Id : Long,
    val player1Name : String,
    val player1WinCount : Int,
    val player1EloScore : Int,
    val player1EloScoreChange : Int,

    val player2Id : Long,
    val player2Name : String,
    val player2WinCount : Int,
    val player2EloScore : Int,
    val player2EloScoreChange : Int,

    val regDateTime : LocalDateTime
) : GameDTO

data class RunningGameDTO(
    val id : Long,

    val player1 : Player,
    val player2 : Player,

    val player1EstimateWinRate : Double?,
    val player2EstimateWinRate : Double?,

    val gameTypeCode : Int
) : GameDTO
