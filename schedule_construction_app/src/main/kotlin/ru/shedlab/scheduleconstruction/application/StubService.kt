package ru.shedlab.scheduleconstruction.application

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.transaction.reactive.executeAndAwait
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.domain.repo.StubRepo
import java.util.UUID

@Service
class StubService(
    private val stubRepo: StubRepo,
    private val operator: TransactionalOperator,
    private val template: R2dbcEntityTemplate,
) {
    suspend fun getStub(id: UUID): Stub? {
        return operator.executeAndAwait {
            // Here make for update call
            template.databaseClient
                .sql("select * from stub where id = '$id' for update")
            stubRepo.findById(id)
        }
    }
}
