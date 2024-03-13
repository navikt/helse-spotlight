package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testmeldinger.halvTime
import no.nav.helse.kafka.river.HverHalvtimeRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.Test

class HverHalvtimeRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        HverHalvtimeRiver(testRapid, mediatorMock)
    }

    @Test
    fun `kan lese inn halv_time`() {
        testRapid.sendTestMessage(halvTime())
        verify(exactly = 1) { mediatorMock.påminnSuspenderteKommandokjeder() }
    }

}