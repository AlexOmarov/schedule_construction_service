package ru.shedlab.scheduleconstruction.domain.repo

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.shedlab.scheduleconstruction.domain.entity.Stub
import java.util.*

@Repository
interface StubRepo : CoroutineCrudRepository<Stub, UUID>
