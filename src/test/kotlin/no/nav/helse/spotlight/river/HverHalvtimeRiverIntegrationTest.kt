package no.nav.helse.spotlight.river

import no.nav.helse.spotlight.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class HverHalvtimeRiverIntegrationTest : AbstractIntegrationTest() {
    @Test
    fun `Sender påminnelse når halv_time leses inn`() {
        // Given:
        val kommandokjede =
            lagretKommandokjede(
                opprettet = LocalDateTime.now().minusMinutes(31),
                antallGangerPåminnet = 0,
            )
        val commandContextId = kommandokjede.commandContextId

        // When:
        testRapid.sendTestMessage("""{ "@event_name": "halv_time" }""")

        // Then:
        assertEquals(1, dao.finn(commandContextId)?.antallGangerPåminnet)

        val påminnelseMelding = testRapid.inspektør.message(0)
        assertEquals("kommandokjede_påminnelse", påminnelseMelding["@event_name"].asText())
        assertEquals(kommandokjede.commandContextId.toString(), påminnelseMelding["commandContextId"].asText())
        assertEquals(kommandokjede.meldingId.toString(), påminnelseMelding["meldingId"].asText())
        assertEquals(1, testRapid.inspektør.size)
    }
}
