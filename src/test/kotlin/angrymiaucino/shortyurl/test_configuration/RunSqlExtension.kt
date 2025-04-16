package angrymiaucino.shortyurl.test_configuration

import io.r2dbc.spi.ConnectionFactory
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ScriptUtils
import reactor.core.publisher.Mono
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class RunSqlExtension : BeforeTestExecutionCallback {
    override fun beforeTestExecution(context: ExtensionContext?) {
        val testMethod = context?.testMethod?.orElse(null) ?: return
        val annotation = testMethod.getAnnotation(RunSql::class.java) ?: return
        val testInstance = context.testInstance.orElseThrow { RuntimeException("Test instance not found. ${javaClass.simpleName} is supposed to be used in JUnit 5 only!") }

        getConnectionFactory(testInstance)?.let { connectionFactory ->
            annotation.scripts.forEach { script ->
                Mono.from(connectionFactory.create())
                    .flatMap { connection -> ScriptUtils.executeSqlScript(connection, ClassPathResource(script)) }
                    .block()
            }
        }
    }

    private fun getConnectionFactory(testInstance: Any): ConnectionFactory? =
        testInstance::class.memberProperties
            .find { it.name == "connectionFactory" }
            ?.apply { isAccessible = true }
            ?.getter
            ?.call(testInstance) as? ConnectionFactory
}
