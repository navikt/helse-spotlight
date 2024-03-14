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
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId))
        assertLagret(commandContextId)
    }

    @Test
    fun `Oppdaterer suspendert kommandokjede on conflict`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId = commandContextId, command =  "EnAnnenCommand"))
        assertOppdatert(commandContextId, "EnAnnenCommand")
    }

    @Test
    fun `Antall ganger påminnet settes til 0 når rad blir oppdatert`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.påminnet(commandContextId)
        assertPåminnet(commandContextId, 1)
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId = commandContextId, command =  "EnAnnenCommand"))
        assertPåminnet(commandContextId, 0)
        assertOppdatert(commandContextId, "EnAnnenCommand")
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede ferdigstilles`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstiltTilDatabase(commandContextId))
        assertSlettet(commandContextId)
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede avbrytes`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(commandContextId))
        kommandokjedeDao.avbrutt(kommandokjedeAvbruttTilDatabase(commandContextId))
        assertSlettet(commandContextId)
    }

    @Test
    fun `Henter suspenderte kommandokjeder som er minst 30 minutter gamle`() {
        val commandContextId = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId))
        val suspenderteKommandokjeder = kommandokjedeDao.hent()
        assertEquals(1, suspenderteKommandokjeder.size)
        assertEquals(commandContextId, suspenderteKommandokjeder.first().commandContextId)
    }

    @Test
    fun `Henter ikke suspenderte kommandokjeder som ikke er 30 minutter gamle`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        val suspenderteKommandokjeder = kommandokjedeDao.hent()
        assertEquals(0, suspenderteKommandokjeder.size)
    }

    @Test
    fun `Inkrementerer antall_ganger_påminnet etter påminnelse`() {
        val commandContextId1 = UUID.randomUUID()
        val commandContextId2 = UUID.randomUUID()
        kommandokjedeDao.upsert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId = commandContextId1))
        kommandokjedeDao.upsert(kommandokjedeSuspendertForOverEnHalvtimeSiden(commandContextId = commandContextId2))
        assertPåminnet(commandContextId1, 0)
        assertPåminnet(commandContextId2, 0)
        kommandokjedeDao.påminnet(commandContextId1)
        assertPåminnet(commandContextId1, 1)
        assertPåminnet(commandContextId2, 0)
    }

}