package angrymiaucino.shortyurl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories
class ShortyUrlApplication

fun main(args: Array<String>) {
    runApplication<ShortyUrlApplication>(*args)
}
