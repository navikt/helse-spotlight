package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testdata.kommandokjedeSuspendertTilDatabase
import no.nav.helse.Testmeldinger.kommandokjedeSuspendert
import no.nav.helse.kafka.river.KommandokjedeSuspendertRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

internal class KommandokjedeSuspendertRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeSuspendertRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn kommandokjede_suspendert`() {
        testRapid.sendTestMessage(kommandokjedeSuspendert())
        verify(exactly = 1) { mediatorMock.kommandokjedeSuspendert(kommandokjedeSuspendertTilDatabase()) }
    }

}