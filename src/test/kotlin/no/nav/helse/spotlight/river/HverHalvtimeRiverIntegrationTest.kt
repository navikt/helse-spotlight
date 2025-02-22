package no.nav.helse.spotlight.river

import no.nav.helse.spotlight.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertNotNull

class HverHalvtimeRiverIntegrationTest : AbstractIntegrationTest() {
    @Test
    fun `Sender påminnelse når halv_time leses inn`() {
        // Given:
        val kommandokjede =
            lagretKommandokjede(
                mottattTidspunkt = Instant.now().minusMinutes(31),
                antallGangerPåminnet = 0,
            )
        val commandContextId = kommandokjede.commandContextId

        // When:
        testRapid.sendTestMessage("""{ "@event_name": "halv_time" }""")

        // Then:
        val lagretKommandokjede = dao.finn(commandContextId)
        assertNotNull(lagretKommandokjede)
        assertEquals(1, lagretKommandokjede.totaltAntallGangerPåminnet)
        assertEquals(1, lagretKommandokjede.sistSuspenderteSti.antallGangerPåminnet)

        val påminnelseMelding = testRapid.inspektør.message(0)
        assertEquals("kommandokjede_påminnelse", påminnelseMelding["@event_name"].asText())
        assertEquals(kommandokjede.commandContextId.toString(), påminnelseMelding["commandContextId"].asText())
        assertEquals(kommandokjede.sisteMeldingId.toString(), påminnelseMelding["meldingId"].asText())
        assertEquals(1, testRapid.inspektør.size)
    }
}
