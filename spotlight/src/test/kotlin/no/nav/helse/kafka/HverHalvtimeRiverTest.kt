package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class HverHalvtimeRiverTest {

    private val testRapid = TestRapid()
    private val mediator = mockk<Mediator>(relaxed = true)

    init {
        HverHalvtimeRiver(testRapid, mediator)
    }

    @Test
    fun `kan lese inn kommandokjede_ferdigstilt`() {
        testRapid.sendTestMessage(halvTime())
        verify(exactly = 1) { mediator.påminnSuspenderteKommandokjeder() }
    }

    @Language("JSON")
    private fun halvTime(): String = """
          {
            "@event_name": "halv_time"
          }
        """.trimIndent()
}