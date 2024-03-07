package supa.dupa.mysqltest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MySqlTestApplication

fun main(args: Array<String>) {
	runApplication<MySqlTestApplication>(*args)
}
