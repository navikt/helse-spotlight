package no.nav.helse

import org.intellij.lang.annotations.Language
import java.time.LocalDateTime
import java.util.*

object Testmeldinger {

    @Language("JSON")
    internal fun kommandokjedeSuspendert(commandContextId: UUID = UUID.randomUUID(), opprettet: LocalDateTime = LocalDateTime.now()): String {
        return """
            {
              "@event_name": "kommandokjede_suspendert",
              "commandContextId": "$commandContextId",
              "meldingId": "${UUID.randomUUID()}",
              "command": "OpprettVedtaksperiodeCommand",
              "sti": [2,2,0],
              "@opprettet": "$opprettet"
            }
        """.trimIndent()
    }

    @Language("JSON")
    internal fun kommandokjedeFerdigstilt(commandContextId: UUID = UUID.randomUUID()): String {
        return """
            {
              "@event_name": "kommandokjede_ferdigstilt",
              "commandContextId": "$commandContextId",
              "meldingId": "${UUID.randomUUID()}",
              "command": "OpprettVedtaksperiodeCommand",
              "@id": "${UUID.randomUUID()}",
              "@opprettet": "${LocalDateTime.now()}"
            }
        """.trimIndent()
    }

    @Language("JSON")
    internal fun kommandokjedeAvbrutt(commandContextId: UUID = UUID.randomUUID()): String {
        return """
            {
              "@event_name": "kommandokjede_avbrutt",
              "commandContextId": "$commandContextId",
              "meldingId": "${UUID.randomUUID()}"
            }
        """.trimIndent()
    }

    @Language("JSON")
    internal fun halvTime(): String = """
          {
            "@event_name": "halv_time"
          }
        """.trimIndent()

    @Language("JSON")
    internal fun helTime(time: Int = 6): String = """
          {
            "@event_name": "hel_time",
            "time": $time,
            "minutt": 0,
            "klokkeslett": "06:00:02.50230667",
            "dagen": "2024-03-07",
            "ukedag": "THURSDAY"
          }
        """.trimIndent()
}