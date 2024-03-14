package no.nav.helse

import no.nav.helse.Testdata.COMMAND_CONTEXT_ID
import no.nav.helse.Testdata.EN_COMMAND
import no.nav.helse.Testdata.MELDING_ID
import no.nav.helse.Testdata.OPPRETTET
import no.nav.helse.Testdata.STI
import org.intellij.lang.annotations.Language
import java.time.LocalDateTime

internal object Testmeldinger {

    @Language("JSON")
    internal fun kommandokjedeSuspendert(opprettet: LocalDateTime = OPPRETTET): String = """
        {
          "@event_name": "kommandokjede_suspendert",
          "commandContextId": "$COMMAND_CONTEXT_ID",
          "meldingId": "$MELDING_ID",
          "command": "$EN_COMMAND",
          "sti": $STI,
          "@opprettet": "$opprettet"
        }
    """.trimIndent()

    @Language("JSON")
    internal fun kommandokjedeFerdigstilt(): String = """
        {
          "@event_name": "kommandokjede_ferdigstilt",
          "commandContextId": "$COMMAND_CONTEXT_ID",
          "meldingId": "$MELDING_ID",
          "command": "$EN_COMMAND",
          "@opprettet": "$OPPRETTET"
        }
    """.trimIndent()

    @Language("JSON")
    internal fun kommandokjedeAvbrutt(): String = """
        {
          "@event_name": "kommandokjede_avbrutt",
          "commandContextId": "$COMMAND_CONTEXT_ID",
          "meldingId": "$MELDING_ID"
        }
    """.trimIndent()

    @Language("JSON")
    internal fun halvTime(): String = """
          {
            "@event_name": "halv_time"
          }
        """.trimIndent()

    @Language("JSON")
    internal fun helTime(): String = """
          {
            "@event_name": "hel_time",
            "time": 6,
            "minutt": 0,
            "klokkeslett": "06:00:02.50230667",
            "dagen": "2024-03-07",
            "ukedag": "THURSDAY"
          }
        """.trimIndent()

}