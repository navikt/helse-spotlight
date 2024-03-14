package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testdata.kommandokjedeFerdigstiltTilDatabase
import no.nav.helse.Testmeldinger.kommandokjedeFerdigstilt
import no.nav.helse.kafka.river.KommandokjedeFerdigstiltRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

internal class KommandokjedeFerdigstiltRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeFerdigstiltRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn kommandokjede_ferdigstilt`() {
        testRapid.sendTestMessage(kommandokjedeFerdigstilt())
        verify(exactly = 1) { mediatorMock.kommandokjedeFerdigstilt(kommandokjedeFerdigstiltTilDatabase()) }
    }

}