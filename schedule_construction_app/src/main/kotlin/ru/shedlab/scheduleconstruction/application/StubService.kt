package ru.shedlab.scheduleconstruction.application

import kotlinx.coroutines.async
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.domain.repo.StubRepo
import ru.shedlab.scheduleconstruction.infrastructure.utils.CoroutineUtils.ioScope
import java.util.UUID
import kotlin.coroutines.EmptyCoroutineContext

@Service
class StubService(
    private val stubRepo: StubRepo,
    private val operator: TransactionalOperator,
    private val template: R2dbcEntityTemplate,
) {
    suspend fun getStub(id: UUID): Stub? {
        // here async because of micrometer simple observation memory leak with r2dbc transactions
        return ioScope.async(EmptyCoroutineContext) {
            return@async operator.executeAndAwait {
                // Here make for update call
                template.databaseClient.sql("select * from stub where id = '$id' for update")
                stubRepo.findById(id)
            }
        }.await()
    }
}
