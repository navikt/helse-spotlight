package no.nav.helse.kafka

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.util.*

class KommandokjedeAvbruttRiverTest {
    private val testRapid = TestRapid()
    private val mediator = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeAvbruttRiver(testRapid, mediator)
    }

    @Test
    fun `kan lese inn kommandokjede_avbrutt`() {
        testRapid.sendTestMessage(kommandokjedeAvbrutt())
        verify(exactly = 1) { mediator.kommandokjedeAvbrutt(any()) }
    }

    @Language("JSON")
    private fun kommandokjedeAvbrutt(): String {
        return """
            {
              "@event_name": "kommandokjede_avbrutt",
              "commandContextId": "${UUID.randomUUID()}",
              "meldingId": "${UUID.randomUUID()}"
            }
        """.trimIndent()
    }
}