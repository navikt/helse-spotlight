package no.nav.helse.spotlight.river

import no.nav.helse.spotlight.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertNotNull

class KommandokjedeSuspendertRiverIntegrationTest : AbstractIntegrationTest() {
    @Test
    fun `Lagrer suspendert kommandokjede når kommandokjede_suspendert leses inn`() {
        // Given:
        val commandContextId = UUID.randomUUID()
        val meldingId = UUID.randomUUID()
        val opprettet = LocalDateTime.now().minusSeconds(1)

        // When:
        testRapid.sendTestMessage(
            """
            {
              "@event_name": "kommandokjede_suspendert",
              "commandContextId": "$commandContextId",
              "meldingId": "$meldingId",
              "command": "EnCommand",
              "sti": [0],
              "@opprettet": "$opprettet"
            }
            """.trimIndent(),
        )

        // Then:
        val lagretKommandokjede = dao.finn(commandContextId)
        assertNotNull(lagretKommandokjede)
        lagretKommandokjede.let {
            assertEquals(commandContextId, it.commandContextId)
            assertEquals(meldingId, it.meldingId)
            assertEquals("EnCommand", it.command)
            assertEquals(listOf(0), it.sti)
            assertEquals(opprettet.roundToMicros(), it.opprettet)
            assertEquals(0, it.antallGangerPåminnet)
        }
    }
}
