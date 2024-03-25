package no.nav.helse.slack

import no.nav.helse.Testdata.kommandokjedeFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMeldingBuilder.byggDagligSlackMelding
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class SlackMeldingBuilderTest {

    @Test
    fun `Sjekker at byggSlackMelding velger riktig tittel`() {
        val enKommandokjede = listOf(kommandokjedeFraDatabase())
        val enKommandokjedeJsonString = enKommandokjede.byggDagligSlackMelding(enKommandokjede.size)
        assertDoesNotThrow { objectMapper.readTree(enKommandokjedeJsonString) }
        assertTrue(enKommandokjedeJsonString.contains("1 kommandokjede"))

        val toKommandokjeder = listOf(kommandokjedeFraDatabase(), kommandokjedeFraDatabase())
        val toKommandokjederJsonString = toKommandokjeder.byggDagligSlackMelding(toKommandokjeder.size)
        assertDoesNotThrow { objectMapper.readTree(toKommandokjederJsonString) }
        assertTrue(toKommandokjederJsonString.contains("2 kommandokjeder"))

        val fortsettelse = listOf(kommandokjedeFraDatabase())
        val fortsettelseJsonString = fortsettelse.byggDagligSlackMelding()
        assertDoesNotThrow { objectMapper.readTree(fortsettelseJsonString) }
        assertTrue(fortsettelseJsonString.contains("Fortsettelse"))
    }

}