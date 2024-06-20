package supa.dupa.mysqltest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import supa.dupa.mysqltest.dto.GameResultDTO
import supa.dupa.mysqltest.dto.RunningGameDTO
import supa.dupa.mysqltest.dto.ServiceResult
import supa.dupa.mysqltest.entities.*
import supa.dupa.mysqltest.repo.CasualGameRepository
import supa.dupa.mysqltest.repo.PlayerRepository
import supa.dupa.mysqltest.repo.RankGameRepository
import supa.dupa.mysqltest.repo.RunningGameRepository
import java.time.OffsetDateTime
import java.time.ZoneId
import kotlin.math.pow
import kotlin.math.roundToInt

@Controller
@RequestMapping(path = ["/match"])
class GameController {

    @Autowired
    private lateinit var runningGameRepository : RunningGameRepository

    @Autowired
    private lateinit var rankGameRepository : RankGameRepository

    @Autowired
    private lateinit var casualGameRepository : CasualGameRepository

    @Autowired
    private lateinit var playerRepository : PlayerRepository

    @PostMapping(path = ["/create"])
    @ResponseBody
    fun createGame(
        @RequestParam(name = "typeCode") typeCode : Int,
        @RequestParam(name = "player1Id") player1Id : Long,
        @RequestParam(name = "player2Id") player2Id : Long
    ) : String {
        val gameType = GameType.entries.find { it.typeCode == typeCode }
            ?: return ServiceResult.Fail(
                code = -1,
                message = "매치 타입이 잘못되었습니다."
            ).toJsonString()

        val player1 = playerRepository.findByIdOrNull(player1Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        val game = runningGameRepository.save(
            RunningGame(
                player1Id = player1Id,
                player2Id = player2Id,
                player1EstimateWinRate = getEstimatedWinRate(player1.eloScore, player2.eloScore),
                player2EstimateWinRate = getEstimatedWinRate(player2.eloScore, player1.eloScore),
                gameTypeCode = gameType.typeCode
            )
        )

        if (game.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        val gameDto = RunningGameDTO(
            id = game.id,
            player1 = player1,
            player2 = player2,
            player1EstimateWinRate = game.player1EstimateWinRate,
            player2EstimateWinRate = game.player2EstimateWinRate,
            gameType = GameType.typeCodeOf(game.gameTypeCode)
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
        ).toJsonString()
    }

    @GetMapping(path = ["/running"])
    @ResponseBody
    fun getRunningGame(
        @RequestParam playerId : Long
    ) : String {
        val game = runningGameRepository.findByPlayerId(playerId)
            ?: return ServiceResult.Success(
                code = 0,
                message = "현재 진행중인 대전이 없습니다",
                data = null
            ).toJsonString()

        if (game.id == null) {
            runningGameRepository.delete(game)

            return ServiceResult.Success(
                code = 0,
                message = "현재 진행중인 대전이 없습니다.",
                data = null
            ).toJsonString()
        }

        val player1 = playerRepository.findByIdOrNull(game.player1Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(game.player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        val gameDto = RunningGameDTO(
            id = game.id,
            player1 = player1,
            player2 = player2,
            player1EstimateWinRate = game.player1EstimateWinRate,
            player2EstimateWinRate = game.player2EstimateWinRate,
            gameType = GameType.typeCodeOf(game.gameTypeCode)
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
        ).toJsonString()
    }

    @PostMapping(path = ["/cancel"])
    @ResponseBody
    fun cancelRunningGame(
        @RequestParam playerId : Long
    ) : String {
        val game = runningGameRepository.findByPlayerId(playerId)
            ?: return ServiceResult.Success(
                code = 0,
                message = "현재 진행중인 대전이 없습니다",
                data = null
            ).toJsonString()

        if (game.id == null) {
            runningGameRepository.delete(game)

            return ServiceResult.Success(
                code = 0,
                message = "현재 진행중인 대전이 없습니다.",
                data = null
            ).toJsonString()
        }

        val player1 = playerRepository.findByIdOrNull(game.player1Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(game.player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        val gameResultDTO = GameResultDTO(
            gameType = GameType.typeCodeOf(game.gameTypeCode),

            player1Id = player1.id,
            player1Name = player1.name,
            player1WinCount = 0,
            player1EloScore = player1.eloScore.roundToInt(),
            player1EloScoreChange = 0,

            player2Id = player2.id,
            player2Name = player2.name,
            player2WinCount = 0,
            player2EloScore = player2.eloScore.roundToInt(),
            player2EloScoreChange = 0,

            regDateTime = OffsetDateTime.now(ZoneId.systemDefault()).toLocalDateTime()
        )

        runningGameRepository.delete(game)

        return ServiceResult.Success(
            code = 0,
            data = gameResultDTO
        ).toJsonString()
    }

    @PostMapping(path = ["/score"])
    @ResponseBody
    fun registerMatchScore(
        @RequestParam playerId : Long,
        @RequestParam player1WinCount : Int,
        @RequestParam player2WinCount : Int
    ) : String {
        if (player1WinCount == player2WinCount) {
            return ServiceResult.Fail(
                code = -1,
                message = "비기는 경우가 없어야합니다."
            ).toJsonString()
        }

        val game = runningGameRepository.findByPlayerId(playerId) ?: return ServiceResult.Fail(
            code = -1,
            message = "매치를 찾을 수 없습니다."
        ).toJsonString()

        return when (game.gameTypeCode) {
            GameType.RANK.typeCode -> registerRankMatchScore(game, player1WinCount, player2WinCount)
            GameType.CASUAL.typeCode -> registerCasualMatchScore(game, player1WinCount, player2WinCount)
            else -> ServiceResult.Fail(code = -1, message = "게임 타입이 잘못되었습니다.").toJsonString()
        }
    }

    @GetMapping(path = ["/resent"])
    @ResponseBody
    fun getResentMatch(
        @RequestParam playerId : Long,
        @RequestParam amount : Int
    ) : String {
        val history = casualGameRepository.findResentGame(playerId, amount)
            .plus(rankGameRepository.findResentGame(playerId, amount))
            .sortedByDescending { it.id }
            .take(amount)

        return ServiceResult.Success(
            code = 0,
            data = history
        ).toJsonString()
    }

    private fun registerRankMatchScore(
        game : RunningGame,
        player1WinCount : Int,
        player2WinCount : Int
    ) : String {
        val isFt5 = (player1WinCount == 5 && player1WinCount > player2WinCount) || (player2WinCount == 5 && player2WinCount > player1WinCount)

        if (!isFt5) {
            return ServiceResult.Fail(
                code = -1,
                message = "랭크게임은 5선승으로 진행되어야 합니다."
            ).toJsonString()
        }

        val rankGame = RankGame(
            id = game.id,
            player1Id = game.player1Id,
            player2Id = game.player2Id,
            player1EstimateWinRate = game.player1EstimateWinRate,
            player2EstimateWinRate = game.player2EstimateWinRate,
            player1WinCount = player1WinCount,
            player2WinCount = player2WinCount
        )

        val saveResult = rankGameRepository.save(rankGame)

        if (saveResult.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        val player1 = playerRepository.findByIdOrNull(game.player1Id)
            ?: return ServiceResult.Fail(
                code = -1, message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(game.player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        val oldPlayer1EloScore = player1.eloScore.roundToInt()
        val oldPlayer2EloScore = player2.eloScore.roundToInt()

        player1.updateEloScore(
            estimatedWinRate = game.player1EstimateWinRate,
            myWinCount = player1WinCount,
            opWinCount = player2WinCount,
        )

        player2.updateEloScore(
            estimatedWinRate = game.player2EstimateWinRate,
            myWinCount = player2WinCount,
            opWinCount = player1WinCount
        )

        playerRepository.saveAll(listOf(player1, player2))
        runningGameRepository.delete(game)

        val gameResultDto = GameResultDTO(
            gameType = GameType.RANK,

            player1Id = player1.id,
            player1Name = player1.name,
            player1WinCount = player1WinCount,
            player1EloScore = player1.eloScore.roundToInt(),
            player1EloScoreChange = player1.eloScore.roundToInt() - oldPlayer1EloScore,

            player2Id = player2.id,
            player2Name = player2.name,
            player2WinCount = player2WinCount,
            player2EloScore = player2.eloScore.roundToInt(),
            player2EloScoreChange = player2.eloScore.roundToInt() - oldPlayer2EloScore,

            regDateTime = saveResult.timestamp.toLocalDateTime()
        )

        return ServiceResult.Success(
            code = 0,
            data = gameResultDto
        ).toJsonString()
    }

    private fun registerCasualMatchScore(
        game : RunningGame,
        player1WinCount : Int,
        player2WinCount : Int
    ) : String {
        val casualGame = CasualGame(
            id = game.id,
            player1Id = game.player1Id,
            player2Id = game.player2Id,
            player1WinCount = player1WinCount,
            player2WinCount = player2WinCount
        )

        val saveResult = casualGameRepository.save(casualGame)

        if (saveResult.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        if (game.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        val player1 = playerRepository.findByIdOrNull(game.player1Id)
            ?: return ServiceResult.Fail(
                code = -1, message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(game.player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        runningGameRepository.delete(game)

        val gameResultDto = GameResultDTO(
            gameType = GameType.CASUAL,

            player1Id = player1.id,
            player1Name = player1.name,
            player1WinCount = player1WinCount,
            player1EloScore = 0,
            player1EloScoreChange = 0,

            player2Id = player2.id,
            player2Name = player2.name,
            player2WinCount = player2WinCount,
            player2EloScore = 0,
            player2EloScoreChange = 0,

            regDateTime = saveResult.timestamp.toLocalDateTime()
        )

        return ServiceResult.Success(
            code = 0,
            data = gameResultDto
        ).toJsonString()
    }

    private fun getEstimatedWinRate(
        myScore : Double,
        opScore : Double
    ) : Double = 1.0 / (10.0.pow((opScore - myScore) / 625.0) + 1)

    private fun Player.updateEloScore(
        estimatedWinRate : Double,
        myWinCount : Int,
        opWinCount : Int
    ) {
        this.eloScore += ((5 * (1 - estimatedWinRate)) * myWinCount) + ((5 * (0 - estimatedWinRate)) * opWinCount)
    }
}
