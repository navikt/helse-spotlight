package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.UUID

class KommandokjedeFerdigstiltRiverTest {

    private val testRapid = TestRapid()
    private val mediator = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeFerdigstiltRiver(testRapid, mediator)
    }

    @Test
    fun `kan lese inn kommandokjede_ferdigstilt`() {
        testRapid.sendTestMessage(kommandokjedeFerdigstilt())
        verify(exactly = 1) { mediator.kommandokjedeFerdigstilt(any()) }
    }

    @Language("JSON")
    private fun kommandokjedeFerdigstilt(): String {
        return """
            {
              "@event_name": "kommandokjede_ferdigstilt",
              "commandContextId": "${UUID.randomUUID()}",
              "meldingId": "${UUID.randomUUID()}",
              "command": "OpprettVedtaksperiodeCommand",
              "@id": "${UUID.randomUUID()}",
              "@opprettet": "${LocalDateTime.now()}"
            }
        """.trimIndent()
    }

}