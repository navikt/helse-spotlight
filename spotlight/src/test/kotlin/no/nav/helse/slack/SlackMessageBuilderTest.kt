package no.nav.helse.slack

import no.nav.helse.Testdata.kommandokjedeSuspendertFraDatabase
import no.nav.helse.objectMapper
import no.nav.helse.slack.SlackMessageBuilder.byggSlackMelding
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class SlackMessageBuilderTest {

    @Test
    fun `Sjekker at byggSlackMelding lager gyldig json`() {
        val jsonString = listOf(kommandokjedeSuspendertFraDatabase(), kommandokjedeSuspendertFraDatabase()).byggSlackMelding()
        assertDoesNotThrow { objectMapper.readTree(jsonString) }
    }
}