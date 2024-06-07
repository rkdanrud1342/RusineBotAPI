package supa.dupa.mysqltest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import supa.dupa.mysqltest.entities.Player
import supa.dupa.mysqltest.dto.PlayerDTO
import supa.dupa.mysqltest.dto.PlayerRankDTO
import supa.dupa.mysqltest.dto.ServiceResult
import supa.dupa.mysqltest.repo.CasualGameRepository
import supa.dupa.mysqltest.repo.PlayerRepository
import supa.dupa.mysqltest.repo.RankGameRepository
import kotlin.math.roundToInt


@Controller
@RequestMapping(path = ["/player"])
class PlayerController {

    @Autowired
    private lateinit var playerRepository : PlayerRepository

    @Autowired
    private lateinit var casualGameRepository : CasualGameRepository

    @Autowired
    private lateinit var rankGameRepository : RankGameRepository


    @PostMapping(path = ["/register"])
    @ResponseBody
    fun registerUser(
        @RequestParam(name = "id") id : Long,
        @RequestParam(name = "playerName") name : String,
        @RequestParam(name = "grade") grade : Int
    ) : String {
        if (playerRepository.findByIdOrNull(id) != null) {
            return ServiceResult.Fail(
                code = -1,
                message = "이미 프로필이 있는뎁쇼?"
            ).toJsonString()
        }

        val saveResult = playerRepository.save(
            Player(
                id = id,
                name = name.ifBlank { "익명의 결투가" },
                eloScore = (1150 + 770 * grade).toDouble()
            )
        )

        val playerDto = PlayerDTO(
            id = saveResult.id,
            name = saveResult.name,
            casualWinCount = 0,
            casualLoseCount = 0,
            rankWinCount = 0,
            rankLoseCount = 0,
            eloScore = saveResult.eloScore.roundToInt()
        )

        return ServiceResult.Success(
            code = 0,
            data = playerDto
        ).toJsonString()
    }

    @GetMapping(path = ["/info"])
    @ResponseBody
    fun getPlayer(
        @RequestParam id : Long
    ) : String {
        val player = playerRepository.findByIdOrNull(id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "선수 검색에 실패했습니다."
            ).toJsonString()

        return ServiceResult.Success(
            code = 0,
            data = player
        ).toJsonString()
    }

    @GetMapping(path = ["/profile"])
    @ResponseBody
    fun getProfile(
        @RequestParam id : Long
    ) : String {
        val player = playerRepository.findByIdOrNull(id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "플레이어 검색에 실패했습니다."
            ).toJsonString()

        val casualGames = casualGameRepository.findAllGame(player.id)
        val rankGames = rankGameRepository.findAllGame(player.id)

        val playerDto = PlayerDTO(
            id = player.id,
            name = player.name,
            casualWinCount = casualGames.count {
                if (it.player1Id == player.id) {
                    it.player1WinCount > it.player2WinCount
                } else {
                    it.player1WinCount < it.player2WinCount
                }
            },
            casualLoseCount = casualGames.count {
                if (it.player1Id == player.id) {
                    it.player1WinCount < it.player2WinCount
                } else {
                    it.player1WinCount > it.player2WinCount
                }
            },
            rankWinCount = rankGames.count {
                if (it.player1Id == player.id) {
                    it.player1WinCount > it.player2WinCount
                } else {
                    it.player1WinCount < it.player2WinCount
                }
            },
            rankLoseCount = rankGames.count {
                if (it.player1Id == player.id) {
                    it.player1WinCount < it.player2WinCount
                } else {
                    it.player1WinCount > it.player2WinCount
                }
            },
            eloScore = player.eloScore.roundToInt()
        )

        return ServiceResult.Success(
            code = 0,
            data = playerDto
        ).toJsonString()
    }

    @GetMapping(path = ["/ranking"])
    @ResponseBody
    fun getPlayerRanking(
        @RequestParam(name = "playerId") id : Long
    ) : String {
        val allPlayers = playerRepository.findAll().sortedByDescending { it.eloScore }.withIndex()

        val playerData = allPlayers.find { it.value.id == id }
            ?: return ServiceResult.Fail(
                code = -1,
                message = "플레이어 정보가 없습니다."
            ).toJsonString()

        val playerRankDto = PlayerRankDTO(
            top10 = allPlayers.take(10).map { it.value },
            player = playerData.value,
            rank = playerData.index
        )

        return ServiceResult.Success(
            code = 0,
            data = playerRankDto,
        ).toJsonString()
    }

    @GetMapping(path = ["/all"])
    @ResponseBody
    fun getAllPlayers() : String {
        val searchResult = playerRepository.findAll()

        val playerList = mutableListOf<Player>()
        searchResult.forEach { p ->
            playerList.add(p)
        }

        return ServiceResult.Success(
            code = 0,
            data = playerList as List<Player>
        ).toJsonString()
    }
}
