package supa.dupa.mysqltest.controller

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import supa.dupa.mysqltest.entities.Player
import supa.dupa.mysqltest.repo.PlayerRepository


@Controller
@RequestMapping(path = ["/player"])
class PlayerController {

    @Autowired
    private lateinit var playerRepository : PlayerRepository

    @PostMapping(path=["/register"])
    @ResponseBody
    fun registerUser(
        @RequestParam name : String,
        @RequestParam grade : Int = 0
    ) : Player {
        val user = Player(
            name = name,
            grade = grade
        )

        return playerRepository.save(user)
    }

    @GetMapping(path=["/all"])
    @ResponseBody
    fun getAllUsers() = Json.encodeToString(playerRepository.findAll())

    @GetMapping(path=["/{id}"])
    @ResponseBody
    fun getUser(
        @PathVariable id : String
    ) = Json.encodeToString(playerRepository.findByIdOrNull(id))
}
