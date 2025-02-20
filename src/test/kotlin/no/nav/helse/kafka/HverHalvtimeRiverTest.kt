package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testmeldinger.halvTime
import no.nav.helse.kafka.river.HverHalvtimeRiver
import org.junit.jupiter.api.Test

internal class HverHalvtimeRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        HverHalvtimeRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn halv_time`() {
        testRapid.sendTestMessage(halvTime())
        verify(exactly = 1) { mediatorMock.påminnKommandokjeder() }
    }

}
