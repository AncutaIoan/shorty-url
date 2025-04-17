package angrymiaucino.shortyurl.repository

import angrymiaucino.shortyurl.test_configuration.TestcontainersConfigurationBase
import angrymiaucino.shortyurl.test_configuration.TestcontainersIntegrationTest
import org.springframework.beans.factory.annotation.Autowired

@TestcontainersIntegrationTest
class ShortLinkRepositoryIntegrationTest: TestcontainersConfigurationBase() {
    @Autowired
    lateinit var repository: ShortLinkRepository


}