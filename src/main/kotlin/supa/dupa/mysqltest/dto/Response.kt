package supa.dupa.mysqltest.dto

import com.google.gson.Gson

sealed class ServiceResult<out T : Any> {
    companion object {
        private val gson = Gson()
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
