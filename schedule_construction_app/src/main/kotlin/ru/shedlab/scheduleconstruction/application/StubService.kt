package ru.shedlab.scheduleconstruction.application

import org.springframework.stereotype.Service
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import ru.shedlab.scheduleconstruction.domain.repo.StubRepo
import java.util.*

@Service
class StubService(private val stubRepo: StubRepo) {
    suspend fun getStub(id: UUID): Stub? {
        return stubRepo.findById(id)
    }
}
