package supa.dupa.mysqltest.repo

import org.springframework.data.repository.CrudRepository
import supa.dupa.mysqltest.entities.Channel

interface ChannelRepository : CrudRepository<Channel, String>