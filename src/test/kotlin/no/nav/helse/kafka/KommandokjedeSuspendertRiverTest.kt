package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testdata.kommandokjedeSuspendertTilDatabase
import no.nav.helse.Testmeldinger.kommandokjedeSuspendert
import no.nav.helse.kafka.river.KommandokjedeSuspendertRiver
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

    @Test
    fun `Leser ikke inn inn kommandokjede_suspendert hvis command = OppdaterPersonsnapshotCommand`() {
        testRapid.sendTestMessage(kommandokjedeSuspendert(command = "OppdaterPersonsnapshotCommand"))
        verify(exactly = 0) { mediatorMock.kommandokjedeSuspendert(any()) }
    }
}
