package no.nav.helse.slack

import no.nav.helse.Testdata.kommandokjedeSuspendertFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMessageBuilder.byggSlackMelding
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

internal class SlackMessageBuilderTest {

    @Test
    fun `Sjekker at byggSlackMelding lager gyldig json`() {
        val suspenderteKommandokjeder = listOf(kommandokjedeSuspendertFraDatabase(), kommandokjedeSuspendertFraDatabase())
        val jsonString = suspenderteKommandokjeder.byggSlackMelding(suspenderteKommandokjeder.size)
        assertDoesNotThrow { objectMapper.readTree(jsonString) }
    }
}