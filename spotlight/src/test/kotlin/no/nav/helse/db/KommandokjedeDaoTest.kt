package no.nav.helse.db

import no.nav.helse.Testdata.kommandokjedeAvbruttTilDatabase
import no.nav.helse.Testdata.kommandokjedeFerdigstiltTilDatabase
import no.nav.helse.Testdata.kommandokjedeSuspendertForOverEnHalvtimeSiden
import no.nav.helse.Testdata.kommandokjedeSuspendertTilDatabase
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class KommandokjedeDaoTest: DatabaseIntegrationTest() {

    @Test
    fun `Kan lagre suspendert kommandokjede`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase(commandContextId))
        assertLagret(commandContextId)
    }

    @Test
    fun `Oppdaterer suspendert kommandokjede on conflict`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase(commandContextId = commandContextId, command =  "EnAnnenCommand"))
        assertOppdatert(commandContextId, "EnAnnenCommand")
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede ferdigstilles`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstiltTilDatabase(commandContextId))
        assertSlettet(commandContextId)
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede avbrytes`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.avbrutt(kommandokjedeAvbruttTilDatabase(commandContextId))
        assertSlettet(commandContextId)
    }

    @Test
    fun `Henter suspenderte kommandokjeder som er minst 30 minutter gamle`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId))
        val suspenderteKommandokjeder = kommandokjedeDao.hentSuspenderteKommandokjeder()
        assertEquals(1, suspenderteKommandokjeder.size)
        assertEquals(commandContextId, suspenderteKommandokjeder.first().commandContextId)
    }

    @Test
    fun `Henter ikke suspenderte kommandokjeder som ikke er 30 minutter gamle`() {
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertTilDatabase())
        val suspenderteKommandokjeder = kommandokjedeDao.hentSuspenderteKommandokjeder()
        assertEquals(0, suspenderteKommandokjeder.size)
    }

    @Test
    fun `antall_ganger_påminnet blir inkrementert`() {
        val commandContextId1 = UUID.randomUUID()
        val commandContextId2 = UUID.randomUUID()
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId = commandContextId1))
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId = commandContextId2))
        assertPåminnet(commandContextId1, 0)
        assertPåminnet(commandContextId2, 0)
        kommandokjedeDao.harBlittPåminnet(commandContextId1)
        assertPåminnet(commandContextId1, 1)
        assertPåminnet(commandContextId2, 0)
    }
}