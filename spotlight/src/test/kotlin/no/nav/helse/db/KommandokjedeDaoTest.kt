package no.nav.helse.db

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

internal class KommandokjedeDaoTest: DatabaseIntegrationTest() {

    @Test
    fun `Kan lagre suspendert kommandokjede`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendert(commandContextId))
        assertLagret(commandContextId)
    }

    private fun kommandokjedeSuspendert(commandContextId: UUID) = KommandokjedeSuspendertForDatabase(
        commandContextId = commandContextId,
        meldingId = UUID.randomUUID(),
        command = "EnCommand",
        sti = listOf(1, 3),
        opprettet = LocalDateTime.now()
    )

    private fun assertLagret(commandContextId: UUID) {
        val kommandokjedeSuspendert = query(
            "select * from kommandokjede_ikke_ferdigstilt where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.uuid("command_context_id")
        }
        assertEquals(commandContextId, kommandokjedeSuspendert)
    }
}