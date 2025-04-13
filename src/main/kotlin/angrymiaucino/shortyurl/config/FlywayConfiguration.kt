package angrymiaucino.shortyurl.config

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
@ConditionalOnProperty(prefix = "spring", name = ["flyway.repair-on-migrate"], havingValue = "true")
class FlywayConfiguration {
    @Bean
    @Primary
    fun cleanMigrateStrategy() = FlywayMigrationStrategy(::repairAndMigrate)

    private fun repairAndMigrate(flyway: Flyway) {
        flyway.repair()
        flyway.migrate()
    }
}