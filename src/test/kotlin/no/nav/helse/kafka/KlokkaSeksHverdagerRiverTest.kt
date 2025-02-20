package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testmeldinger.helTime
import no.nav.helse.kafka.river.KlokkaSeksHverdagerRiver
import org.junit.jupiter.api.Test

internal class KlokkaSeksHverdagerRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KlokkaSeksHverdagerRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn hel_time`() {
        testRapid.sendTestMessage(helTime())
        verify(exactly = 1) { mediatorMock.fortellOmKommandokjeder() }
    }

}
