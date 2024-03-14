package no.nav.helse.db

import no.nav.helse.Testdata.COMMAND_CONTEXT_ID
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
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        assertLagret()
    }

    @Test
    fun `Oppdaterer suspendert kommandokjede on conflict`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(command =  "EnAnnenCommand"))
        assertOppdatert(command = "EnAnnenCommand")
    }

    @Test
    fun `Antall ganger påminnet settes til 0 når rad blir oppdatert`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        kommandokjedeDao.påminnet(COMMAND_CONTEXT_ID)
        assertPåminnet(forventetAntallGangerPåminnet =  1)
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase(command =  "EnAnnenCommand"))
        assertPåminnet(forventetAntallGangerPåminnet = 0)
        assertOppdatert(command = "EnAnnenCommand")
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede ferdigstilles`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstiltTilDatabase())
        assertSlettet()
    }

    @Test
    fun `Sletter fra tabellen når kommandokjede avbrytes`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertTilDatabase())
        kommandokjedeDao.avbrutt(kommandokjedeAvbruttTilDatabase())
        assertSlettet()
    }

    @Test
    fun `Henter suspenderte kommandokjeder som er minst 30 minutter gamle`() {
        kommandokjedeDao.upsert(kommandokjedeSuspendertForOverEnHalvtimeSiden())
        val suspenderteKommandokjeder = kommandokjedeDao.hent()
        assertEquals(1, suspenderteKommandokjeder.size)
        assertEquals(COMMAND_CONTEXT_ID, suspenderteKommandokjeder.first().commandContextId)
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