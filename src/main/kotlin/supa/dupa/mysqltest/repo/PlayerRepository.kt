package supa.dupa.mysqltest.repo

import org.springframework.data.repository.CrudRepository
import supa.dupa.mysqltest.entities.Player

interface PlayerRepository : CrudRepository<Player, String>
