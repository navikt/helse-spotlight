package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

class KommandokjedeSuspendertRiverTest {

    private val testRapid = TestRapid()
    private val mediator = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeSuspendertRiver(testRapid, mediator)
    }

    @Test
    fun `kan lese inn kommandokjede_suspendert`() {
        testRapid.sendTestMessage(kommandokjedeSuspendert())
        verify(exactly = 1) { mediator.kommandokjedeSuspendert(any()) }
    }

    @Language("JSON")
    private fun kommandokjedeSuspendert(): String {
        return """
            {
              "@event_name": "kommandokjede_suspendert",
              "commandContextId": "${UUID.randomUUID()}",
              "meldingId": "${UUID.randomUUID()}",
              "command": "OpprettVedtaksperiodeCommand",
              "sti": [2,2,0],
              "@opprettet": "${LocalDateTime.now()}"
            }
        """.trimIndent()
    }

}