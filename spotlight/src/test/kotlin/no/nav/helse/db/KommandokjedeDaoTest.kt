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

    @Test
    fun `Sletter fra tabellen når kommandokjede ferdigstilles`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendert(commandContextId))
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstilt(commandContextId))
        assertSlettet(commandContextId)
    }

    private fun kommandokjedeSuspendert(commandContextId: UUID) = KommandokjedeSuspendertForDatabase(
        commandContextId = commandContextId,
        meldingId = UUID.randomUUID(),
        command = "EnCommand",
        sti = listOf(1, 3),
        opprettet = LocalDateTime.now()
    )

    private fun kommandokjedeFerdigstilt(commandContextId: UUID) = KommandokjedeFerdigstiltForDatabase(
        commandContextId = commandContextId,
        meldingId = UUID.randomUUID(),
        command = "EnCommand",
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

    private fun assertSlettet(commandContextId: UUID) {
        val antall = query(
            "select count(1) from kommandokjede_ikke_ferdigstilt where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single { it.int(1) }
        assertEquals(antall, 0)
    }
}