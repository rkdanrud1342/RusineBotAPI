package supa.dupa.mysqltest.dto

import com.google.gson.GsonBuilder
import supa.dupa.mysqltest.serialization.LocalDateTimeDeserializer
import supa.dupa.mysqltest.serialization.LocalDateTimeSerializer
import java.time.LocalDateTime

sealed class ServiceResult<out T : Any> {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeDeserializer())
            .create()
    }

    abstract val code : Int
    abstract val message : String?

    data class Success<out T : Any>(
        override val code : Int,
        override val message : String? = null,
        val data: T?
    ) : ServiceResult<T>()

    data class Fail(
        override val code : Int,
        override val message : String?
    ) : ServiceResult<Nothing>()

    fun toJsonString() : String = gson.toJson(this)
}
