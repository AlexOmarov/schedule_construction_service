package ru.shedlab.scheduleconstruction.integration.base

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.*
import org.junit.runner.RunWith
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
import org.testcontainers.junit.jupiter.Testcontainers
import ru.shedlab.scheduleconstruction.system.config.AppProps
import java.util.*

@Testcontainers
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class BaseIntegrationTest : TestContainers() {

    // Util system beans

    @Autowired
    lateinit var webClient: WebTestClient

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var props: AppProps

    @Autowired
    lateinit var dbClient: DatabaseClient

    // Spy beans

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

    // this method for add custom logic before test for controllers
    // override it in controller if you want
    fun setupBeforeAll() {
        // overwrite
    }

    fun setupBeforeEach() {

    }

    // this method for add custom logic after test for controllers
    // override it in controller if you want
    fun cleanAfterEach() {
        dbClient.sql { "SELECT * from schedule_construction_service.classroom" }.then().block()
    }

    fun cleanAfterAll() {
        // overwrite
    }
}
