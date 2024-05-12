package supa.dupa.mysqltest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import supa.dupa.mysqltest.entities.*
import supa.dupa.mysqltest.repo.CasualGameRepository
import supa.dupa.mysqltest.repo.RankGameRepository
import supa.dupa.mysqltest.repo.PlayerRepository
import kotlin.math.abs
import kotlin.math.pow

@Controller
@RequestMapping(path = ["/match"])
class GameController {

    @Autowired
    private lateinit var rankGameRepository : RankGameRepository

    @Autowired
    private lateinit var casualGameRepository : CasualGameRepository

    @Autowired
    private lateinit var playerRepository : PlayerRepository

    @PostMapping(path=["/casual/create"])
    @ResponseBody
    fun createCasualGame(
        @RequestParam player1Id : Long,
        @RequestParam player2Id : Long
    ) : String {
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

        val game = casualGameRepository.save(
            CasualGame(
                player1Id = player1.id,
                player2Id = player2.id,
                player1WinCount = player1.casualWinCount,
                player2WinCount = player2.casualWinCount
            )
        )

        return ServiceResult.Success(
            code = 0,
            data = game
        ).toJsonString()
    }

    @PostMapping(path=["/casual/score"])
    @ResponseBody
    fun registerCasualMatchScore(
        @RequestParam gameId : Long,
        @RequestParam player1WinCount : Int,
        @RequestParam player2WinCount : Int
    ) : String {
        casualGameRepository.findByIdOrNull(gameId)?.let { g ->
            g.player1WinCount = player1WinCount
            g.player2WinCount = player2WinCount

            casualGameRepository.save(g)
        } ?: return ServiceResult.Fail(
            code = -1,
            message = "매치를 찾을 수 없습니다."
        ).toJsonString()

        return ServiceResult.Success(
            code = 0,
            data = null
        ).toJsonString()
    }

    @GetMapping(path=["/casual/resent10"])
    @ResponseBody
    fun getResent10CasualMatch(
        @RequestParam playerId : Long,
    ) : String {
        val history = casualGameRepository.findResent10Game(playerId)

        return ServiceResult.Success(
            code = 0,
            data = history
        ).toJsonString()
    }

    @PostMapping(path=["/rank/create"])
    @ResponseBody
    fun registerRankMatch(
        @RequestParam player1Id : Long,
        @RequestParam player2Id : Long
    ) : String {
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

        val game = rankGameRepository.save(
            RankGame(
                gameType = "RANK",
                player1Id = player1Id,
                player2Id = player2Id,
                player1EstimateWinRate = getEstimatedWinRate(player1.eloScore, player2.eloScore),
                player2EstimateWinRate = getEstimatedWinRate(player2.eloScore, player1.eloScore),
                player1WinCount = player1.rankWinCount,
                player2WinCount = player2.rankWinCount
            )
        )

        return ServiceResult.Success(
            code = 0,
            data = game
        ).toJsonString()
    }

    @PostMapping(path=["/rank/score"])
    @ResponseBody
    fun registerRankMatchScore(
        @RequestParam gameId : Long,
        @RequestParam player1WinCount : Int,
        @RequestParam player2WinCount : Int
    ) : String {
        val game = rankGameRepository.findByIdOrNull(gameId)?.let { g ->
            g.player1WinCount = player1WinCount
            g.player2WinCount = player2WinCount

            if (g.player1EstimateWinRate == null || g.player2EstimateWinRate == null) {
                return ServiceResult.Fail(
                    code = -1,
                    message = "매치 정보가 잘못되었습니다."
                ).toJsonString()
            }

            rankGameRepository.save(g)
        } ?: return ServiceResult.Fail(
            code = -1,
            message = "매치를 찾을 수 없습니다."
        ).toJsonString()

        val player1 = playerRepository.findByIdOrNull(game.player1Id)
            ?: return ServiceResult.Fail(
                code = -1, message = "p1 정보를 찾을 수 없습니다."
            ).toJsonString()

        val player2 = playerRepository.findByIdOrNull(game.player2Id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "p2 정보를 찾을 수 없습니다."
            ).toJsonString()

        player1.eloScore = getEloScore(
            currentScore = player1.eloScore,
            estimatedWinRate = game.player1EstimateWinRate!!,
            myWinCount = player1WinCount,
            opWinCount = player2WinCount,
        )

        player2.eloScore = getEloScore(
            currentScore = player2.eloScore,
            estimatedWinRate = game.player2EstimateWinRate!!,
            myWinCount = player2WinCount,
            opWinCount = player1WinCount
        )

        playerRepository.saveAll(listOf(player1, player2))

        return ServiceResult.Success(
            code = 0,
            data = null
        ).toJsonString()
    }

    @GetMapping(path=["/rank/resent10"])
    @ResponseBody
    fun getResent10RankMatch(
        @RequestParam playerId : Long,
    ) : String {
        val history = rankGameRepository.findResent10Game(playerId)

        if (history.isEmpty()) {
            return ServiceResult.Fail(
                code = -1,
                message = "검색 결과가 없습니다."
            ).toJsonString()
        }

        return ServiceResult.Success(
            code = 0,
            data = history
        ).toJsonString()
    }


    private fun getEstimatedWinRate(
        myScore : Double,
        opScore : Double
    ) : Double = 100.0 / (10.0.pow((opScore - myScore) / 800.0) + 1)

    private fun getEloScore(
        currentScore : Double,
        estimatedWinRate : Double,
        myWinCount : Int,
        opWinCount : Int
    ) : Double {
        val w = if (myWinCount > opWinCount) 1 else 0
        val k = 20

        val additionalScore = abs(myWinCount - opWinCount) * (w - estimatedWinRate) * k

        return currentScore + additionalScore
    }
}
