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
                player2Id = player2.id
            )
        )

        if (game.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        val gameDto = CasualGameDTO(
            game.id,
            player1,
            player2
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
        ).toJsonString()
    }

    @PostMapping(path=["/casual/score"])
    @ResponseBody
    fun registerCasualMatchScore(
        @RequestParam gameId : Long,
        @RequestParam player1WinCount : Int,
        @RequestParam player2WinCount : Int
    ) : String {
        val game = casualGameRepository.findByIdOrNull(gameId)?.let { g ->
            g.player1WinCount = player1WinCount
            g.player2WinCount = player2WinCount

            casualGameRepository.save(g)
        } ?: return ServiceResult.Fail(
            code = -1,
            message = "매치를 찾을 수 없습니다."
        ).toJsonString()

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

        val gameDto = CasualGameDTO(
            game.id,
            player1,
            player2,
            game.player1WinCount,
            game.player2WinCount
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
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
    fun createRankMatch(
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
                player1Id = player1Id,
                player2Id = player2Id,
                player1EstimateWinRate = getEstimatedWinRate(player1.eloScore, player2.eloScore),
                player2EstimateWinRate = getEstimatedWinRate(player2.eloScore, player1.eloScore),
            )
        )

        if (game.id == null) {
            return ServiceResult.Fail(
                code = -1,
                message = "게임 저장에 실패했습니다."
            ).toJsonString()
        }

        val gameDto = RankGameDTO(
            id = game.id,
            player1 = player1,
            player2 = player2,
            player1EstimateWinRate = game.player1EstimateWinRate,
            player2EstimateWinRate = game.player2EstimateWinRate,
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
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

            rankGameRepository.save(g)
        } ?: return ServiceResult.Fail(
            code = -1,
            message = "매치를 찾을 수 없습니다."
        ).toJsonString()

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

        val gameDto = RankGameDTO(
            game.id,
            player1,
            player2,
            game.player1EstimateWinRate,
            game.player2EstimateWinRate,
            game.player1WinCount,
            game.player2WinCount
        )

        return ServiceResult.Success(
            code = 0,
            data = gameDto
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
    ) : Double = 1.0 / (10.0.pow((opScore - myScore) / 1250.0) + 1)

    private fun Player.updateEloScore(
        estimatedWinRate : Double,
        myWinCount : Int,
        opWinCount : Int
    ) {
        this.eloScore += ((20 * (1 - estimatedWinRate)) * myWinCount) + ((20 * (0 - estimatedWinRate)) * opWinCount)
    }
}
