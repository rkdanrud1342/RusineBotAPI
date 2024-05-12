package supa.dupa.mysqltest.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import supa.dupa.mysqltest.entities.Player
import supa.dupa.mysqltest.entities.ServiceResult
import supa.dupa.mysqltest.repo.PlayerRepository


@Controller
@RequestMapping(path = ["/player"])
class PlayerController {

    @Autowired
    private lateinit var playerRepository : PlayerRepository

    @PostMapping(path=["/register"])
    @ResponseBody
    fun registerUser(
        @RequestParam(name = "id") id : Long,
        @RequestParam(name = "playerName") name : String
    ) : String {
        if (playerRepository.findByIdOrNull(id) != null) {
            return ServiceResult.Fail(
                code = -1,
                message = "이미 프로필이 있어요."
            ).toJsonString()
        }

        val saveResult = playerRepository.save(
            Player(
                id = id,
                name = name.ifBlank { "익명의 결투가" }
            )
        )

        return ServiceResult.Success(
            code = 0,
            data = saveResult
        ).toJsonString()
    }

    @GetMapping(path=["/info"])
    @ResponseBody
    fun getPlayer(
        @RequestParam id : Long
    ) : String {
        val player = playerRepository.findByIdOrNull(id)
            ?: return ServiceResult.Fail(
                code = -1,
                message = "플레이어 검색에 실패했습니다."
            ).toJsonString()

        return ServiceResult.Success(
            code = 0,
            data = player
        ).toJsonString()
    }

    @GetMapping(path=["/all"])
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
