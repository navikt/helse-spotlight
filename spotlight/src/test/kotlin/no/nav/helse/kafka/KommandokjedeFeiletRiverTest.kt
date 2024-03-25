package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testdata.kommandokjedeFeiletTilDatabase
import no.nav.helse.Testmeldinger.kommandokjedeFeilet
import no.nav.helse.kafka.river.KommandokjedeFeiletRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

internal class KommandokjedeFeiletRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeFeiletRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn kommandokjede_feilet`() {
        testRapid.sendTestMessage(kommandokjedeFeilet())
        verify(exactly = 1) { mediatorMock.kommandokjedeFeilet(kommandokjedeFeiletTilDatabase()) }
    }

}