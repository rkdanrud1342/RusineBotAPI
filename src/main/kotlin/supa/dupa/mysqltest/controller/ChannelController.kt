package supa.dupa.mysqltest.controller

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import supa.dupa.mysqltest.entities.Channel
import supa.dupa.mysqltest.repo.ChannelRepository

@Controller
@RequestMapping(path = ["/channel"])
class ChannelController {

    @Autowired
    private lateinit var channelRepository : ChannelRepository

    @PostMapping(path=["/register"])
    @ResponseBody
    fun registerChannel(
        @RequestParam serverId : ULong,
        @RequestParam channelId : ULong
    ) : Channel? = channelRepository.save(
        Channel(
            serverId,
            channelId
        )
    )

    @GetMapping(path=["/all"])
    @ResponseBody
    fun getChannels() : String = Json.encodeToString(channelRepository.findAll())
}
