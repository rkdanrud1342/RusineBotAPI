package supa.dupa.mysqltest.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class Channel(
    @Id
    var serverId : ULong,
    var channelId : ULong,
)