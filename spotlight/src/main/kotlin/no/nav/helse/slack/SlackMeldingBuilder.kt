package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import org.intellij.lang.annotations.Language
import java.time.format.DateTimeFormatter

internal object SlackMeldingBuilder {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    internal fun List<KommandokjedeSuspendertFraDatabase>.byggSlackMelding(totaltAntall: Int? = null): String =
        attachments(buildSections(this), totaltAntall)

    private fun buildSections(suspenderteKommandokjeder: List<KommandokjedeSuspendertFraDatabase>): String {
        val iterator = suspenderteKommandokjeder.iterator()
        return buildString {
            iterator.forEach {
                append(section(it))
                if (iterator.hasNext()) append(",")
            }
        }
    }

    @Language("JSON")
    private fun attachments(sections: String, antall: Int?): String = """
        [
          {
            "color": "#36b528",
            "blocks": [
              {
                "type": "section",
                "text": {
                  "type": "mrkdwn",
                  "text": "${lagMeldingTittel(antall)}"
                }
              },
              $sections
            ]
          }
        ]
    """.trimIndent()

    private fun lagMeldingTittel(antall: Int?): String =
        if (antall != null) {
            ":spotlight: Det er $antall ${if (antall == 1) "kommandokjede" else "kommandokjeder"} som sitter fast: :spotlight:"
        } else {
            ":the-more-you-know: Fortsettelse: :the-more-you-know:"
        }

    @Language("JSON")
    private fun section(kommandokjedeSuspendertTilDatabase: KommandokjedeSuspendertFraDatabase) = """
        {
          "type": "section",
          "fields": [
            {
              "type": "mrkdwn",
              "text": "*Command context id:*\n<${link(kommandokjedeSuspendertTilDatabase.commandContextId.toString())}|${kommandokjedeSuspendertTilDatabase.commandContextId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Melding id:*\n<${link(kommandokjedeSuspendertTilDatabase.meldingId.toString())}|${kommandokjedeSuspendertTilDatabase.meldingId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Command:*\n${kommandokjedeSuspendertTilDatabase.command}"
            },
            {
              "type": "mrkdwn",
              "text": "*Sti:*\n${kommandokjedeSuspendertTilDatabase.sti}"
            },
            {
              "type": "mrkdwn",
              "text": "*Tidspunkt:*\n${kommandokjedeSuspendertTilDatabase.opprettet.format(formatter)}"
            },
            {
              "type": "mrkdwn",
              "text": "*Antall ganger påminnet:*\n${kommandokjedeSuspendertTilDatabase.antallGangerPåminnet}"
            }
          ]
        }
    """.trimIndent()

    private fun link(query: String) =
        "https://logs.adeo.no/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-7d%2Fd,to:now))&_a=(columns:!(level,message,envclass,application,pod),filters:!(),hideChart:!f,index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:kuery,query:%22$query%22),sort:!(!('@timestamp',desc)))"
}