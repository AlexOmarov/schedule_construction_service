package ru.shedlab.scheduleconstruction.base

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.cache.CacheManager
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import reactor.kafka.sender.KafkaSender
import ru.shedlab.scheduleconstruction.config.GrpcTestClient
import ru.shedlab.scheduleconstruction.infrastructure.config.Props
import ru.shedlab.scheduleconstruction.presentation.kafka.RetryEvent

@Testcontainers
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BaseIntegrationTest {

    private val logger = LoggerFactory.getLogger(BaseIntegrationTest::class.java)

    // Util system beans

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var props: Props

    @Autowired
    lateinit var dbClient: DatabaseClient

    @Autowired
    lateinit var grpcTestClient: GrpcTestClient

    // Mock beans

    // Spy beans

    @Autowired
    lateinit var retryEventSender: KafkaSender<String, RetryEvent<Any>>

    @SpyBean
    lateinit var cacheManager: CacheManager

    @BeforeAll
    fun setUp() {
        setupBeforeAll()
    }

    @BeforeEach
    fun set() {
        setupBeforeEach()
    }

    @AfterEach
    fun cleanUp() {
        cleanAfterEach()
    }

    @AfterAll
    fun tearDown() {
        cleanAfterAll()
    }

    // this method for add custom logic before test for controllers. Override it in controller if you want
    fun setupBeforeAll() {
        logger.info("setupBeforeAll")
        // overwrite
    }

    fun setupBeforeEach() {
        logger.info("setupBeforeEach")
        // overwrite
    }

    // this method for add custom logic after test for controllers
    // override it in controller if you want
    fun cleanAfterEach() {
        dbClient.sql { "SELECT * from shedlock" }.then().block()
    }

    fun cleanAfterAll() {
        logger.info("cleanAfterAll")
        // overwrite
    }

    companion object {
        private val postgresql = PostgreSQLContainer<Nothing>("postgres:15.2").apply {
            withReuse(true)
            withInitScript("db/setup.sql")
            start()
        }

        private val kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.3.3")).apply {
            withReuse(true)
            start()
        }

        init {
            System.setProperty("spring.flyway.url", postgresql.jdbcUrl)
            System.setProperty("spring.flyway.user", postgresql.username)
            System.setProperty("spring.flyway.password", postgresql.password)
            System.setProperty(
                "spring.r2dbc.url",
                "r2dbc:postgresql://${postgresql.host}:${postgresql.firstMappedPort}/${postgresql.databaseName}"
            )
            System.setProperty("spring.r2dbc.username", postgresql.username)
            System.setProperty("spring.r2dbc.password", postgresql.password)
            System.setProperty("kafka.brokers", kafka.bootstrapServers)
        }
    }
}
