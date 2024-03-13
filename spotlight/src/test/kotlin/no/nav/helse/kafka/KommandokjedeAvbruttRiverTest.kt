package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testmeldinger.kommandokjedeAvbrutt
import no.nav.helse.kafka.river.KommandokjedeAvbruttRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

class KommandokjedeAvbruttRiverTest {
    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeAvbruttRiver(testRapid, mediatorMock)
    }

    @Test
    fun `kan lese inn kommandokjede_avbrutt`() {
        testRapid.sendTestMessage(kommandokjedeAvbrutt())
        verify(exactly = 1) { mediatorMock.kommandokjedeAvbrutt(any()) }
    }

}