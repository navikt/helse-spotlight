package no.nav.helse.spotlight.river

import no.nav.helse.spotlight.AbstractIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*
import kotlin.test.assertNotNull

class HverHalvtimeRiverIntegrationTest : AbstractIntegrationTest() {
    @Test
    fun `Sender påminnelse uten partisjonsnøkkel når vi ikke har en`() {
        // Given:
        val kommandokjede =
            lagretKommandokjede(
                mottattTidspunkt = Instant.now().minusMinutes(31),
                sistePartisjonsnøkkel = null,
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

        assertNull(testRapid.inspektør.key(0))
        val påminnelseMelding = testRapid.inspektør.message(0)
        assertEquals("kommandokjede_påminnelse", påminnelseMelding["@event_name"].asText())
        assertEquals(kommandokjede.commandContextId.toString(), påminnelseMelding["commandContextId"].asText())
        assertEquals(kommandokjede.sisteMeldingId.toString(), påminnelseMelding["meldingId"].asText())
        assertEquals(1, testRapid.inspektør.size)
    }

    @Test
    fun `Sender påminnelse med siste mottatte partisjonsnøkkel når vi har en`() {
        // Given:
        val sistePartisjonsnøkkel = "${UUID.randomUUID()}"
        val kommandokjede =
            lagretKommandokjede(
                mottattTidspunkt = Instant.now().minusMinutes(31),
                sistePartisjonsnøkkel = sistePartisjonsnøkkel,
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

        assertEquals(sistePartisjonsnøkkel, testRapid.inspektør.key(0))
        val påminnelseMelding = testRapid.inspektør.message(0)
        assertEquals("kommandokjede_påminnelse", påminnelseMelding["@event_name"].asText())
        assertEquals(kommandokjede.commandContextId.toString(), påminnelseMelding["commandContextId"].asText())
        assertEquals(kommandokjede.sisteMeldingId.toString(), påminnelseMelding["meldingId"].asText())
        assertEquals(1, testRapid.inspektør.size)
    }
}
