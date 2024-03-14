package no.nav.helse.slack

import no.nav.helse.Testdata.kommandokjedeSuspendertFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMessageBuilder.byggSlackMelding
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class SlackMessageBuilderTest {

    @Test
    fun `Sjekker at byggSlackMelding velger riktig tittel`() {
        val enKommandokjede = listOf(kommandokjedeSuspendertFraDatabase())
        val enKommandokjedeJsonString = enKommandokjede.byggSlackMelding(enKommandokjede.size)
        assertDoesNotThrow { objectMapper.readTree(enKommandokjedeJsonString) }
        assertTrue(enKommandokjedeJsonString.contains("1 kommandokjede"))

        val toKommandokjeder = listOf(kommandokjedeSuspendertFraDatabase(), kommandokjedeSuspendertFraDatabase())
        val toKommandokjederJsonString = toKommandokjeder.byggSlackMelding(toKommandokjeder.size)
        assertDoesNotThrow { objectMapper.readTree(toKommandokjederJsonString) }
        assertTrue(toKommandokjederJsonString.contains("2 kommandokjeder"))

        val fortsettelse = listOf(kommandokjedeSuspendertFraDatabase())
        val fortsettelseJsonString = fortsettelse.byggSlackMelding()
        assertDoesNotThrow { objectMapper.readTree(fortsettelseJsonString) }
        assertTrue(fortsettelseJsonString.contains("Fortsettelse"))
    }

}