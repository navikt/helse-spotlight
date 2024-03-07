package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class HelTimeRiverTest {

    private val testRapid = TestRapid()
    private val mediator = mockk<Mediator>(relaxed = true)

    init {
        HelTimeRiver(testRapid, mediator)
    }

    @Test
    fun `kan lese inn kommandokjede_ferdigstilt`() {
        testRapid.sendTestMessage(helTime())
        verify(exactly = 1) { mediator.fortellOmSuspenderteKommandokjeder() }
    }

    @Language("JSON")
    private fun helTime(): String = """
          {
            "@event_name": "hel_time",
            "time": 6,
            "minutt": 0,
            "klokkeslett": "06:00:02.50230667",
            "dagen": "2024-03-07",
            "ukedag": "THURSDAY"
          }
        """.trimIndent()
}