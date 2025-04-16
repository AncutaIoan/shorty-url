package angrymiaucino.shortyurl

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties
class ShortyUrlApplication

fun main(args: Array<String>) {
    runApplication<ShortyUrlApplication>(*args)
}
