package angrymiaucino.shortyurl.test_configuration

import angrymiaucino.shortyurl.config.R2DBCConfiguration
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.annotation.Import

@DataR2dbcTest
@Target(AnnotationTarget.CLASS)
@ExtendWith(RunSqlExtension::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(R2DBCConfiguration::class)
@Tag("integration-test")
annotation class TestcontainersIntegrationTest
