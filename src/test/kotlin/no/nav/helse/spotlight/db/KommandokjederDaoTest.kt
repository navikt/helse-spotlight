package no.nav.helse.spotlight.db

import no.nav.helse.spotlight.AbstractIntegrationTest
import no.nav.helse.spotlight.Kommandokjede
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

class KommandokjederDaoTest : AbstractIntegrationTest() {
    @Test
    fun `Lagrer ny kommandokjede`() {
        // Given:
        val kommandokjede = Kommandokjede(
            commandContextId = UUID.randomUUID(),
            meldingId = UUID.randomUUID(),
            command = "EnCommand",
            sti = listOf(0),
            opprettet = LocalDateTime.now(),
            antallGangerPåminnet = 0
        )
        val commandContextId = kommandokjede.commandContextId

        // When:
        dao.lagre(kommandokjede)

        // Then:
        assertEquals(kommandokjede.roundedToMicros(), dao.finn(commandContextId))
    }

    @Test
    fun `Henter kommandokjede som er mer enn 30 minutter gammel`() {
        // Given:
        val kommandokjede = lagretKommandokjede(
            opprettet = LocalDateTime.now().minusMinutes(31)
        )

        // When:
        val kommandokjederEldreEnnEnHalvtime = dao.finnAlleEldreEnnEnHalvtime()

        // Then:
        assertEquals(1, kommandokjederEldreEnnEnHalvtime.size)
        assertEquals(kommandokjede.roundedToMicros(), kommandokjederEldreEnnEnHalvtime.first())
    }

    @Test
    fun `Henter ikke kommandokjede som er mindre enn 30 minutter gammel`() {
        // Given:
        lagretKommandokjede(
            opprettet = LocalDateTime.now().minusMinutes(29)
        )

        // When:
        val kommandokjederEldreEnnEnHalvtime = dao.finnAlleEldreEnnEnHalvtime()

        // Then:
        assertEquals(0, kommandokjederEldreEnnEnHalvtime.size)
    }

    @Test
    fun `Oppdaterer kommandokjede som allerede finnes`() {
        // Given:
        val kommandokjede = lagretKommandokjede()
        val commandContextId = kommandokjede.commandContextId
        val oppdatertKommandokjede = kommandokjede.copy(command = "EnAnnenCommand")

        // When:
        dao.lagre(oppdatertKommandokjede)

        // Then:
        assertEquals(oppdatertKommandokjede.roundedToMicros(), dao.finn(commandContextId))
    }

    @Test
    fun `Sletter kommandokjede`() {
        // Given:
        val kommandokjede = lagretKommandokjede()
        val commandContextId = kommandokjede.commandContextId

        // When:
        dao.slett(commandContextId)

        // Then:
        assertEquals(null, dao.finn(commandContextId))
    }
}
