package no.nav.helse.spotlight.slack

import no.nav.helse.spotlight.SuspendertKommandokjede
import org.intellij.lang.annotations.Language
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object SlackMeldingBuilder {
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    fun List<SuspendertKommandokjede>.byggDagligSlackMelding(totaltAntall: Int? = null): String =
        attachments(buildSections(this), lagDagligMeldingTittel(totaltAntall))

    private fun buildSections(kommandokjeder: List<SuspendertKommandokjede>): String {
        val iterator = kommandokjeder.iterator()
        return buildString {
            iterator.forEach {
                append(section(it))
                if (iterator.hasNext()) append(",")
            }
        }
    }

    @Language("JSON")
    private fun attachments(
        sections: String,
        tittel: String,
    ): String =
        """
        [
          {
            "color": "#36b528",
            "blocks": [
              {
                "type": "section",
                "text": {
                  "type": "mrkdwn",
                  "text": "$tittel"
                }
              },
              $sections
            ]
          }
        ]
        """.trimIndent()

    private fun lagDagligMeldingTittel(antall: Int?): String =
        if (antall != null) {
            ":spotlight: Det er $antall ${if (antall == 1) "kommandokjede" else "kommandokjeder"} som sitter fast: :spotlight:"
        } else {
            ":the-more-you-know: Fortsettelse: :the-more-you-know:"
        }

    @Language("JSON")
    private fun section(kommandokjede: SuspendertKommandokjede) =
        """
        {
          "type": "section",
          "fields": [
            {
              "type": "mrkdwn",
              "text": "*Command context id:*\n<${link(kommandokjede.commandContextId.toString())}|${kommandokjede.commandContextId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Melding id:*\n<${link(kommandokjede.sisteMeldingId.toString())}|${kommandokjede.sisteMeldingId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Command:*\n${kommandokjede.command}"
            },
            {
              "type": "mrkdwn",
              "text": "*Sti:*\n${kommandokjede.sistSuspenderteSti.sti}"
            },
            {
              "type": "mrkdwn",
              "text": "*Opprettet:*\n${
            kommandokjede.sistSuspenderteSti.førsteTidspunkt.atZone(ZoneId.of("Europe/Oslo")).format(formatter)
        }"
            },
            {
              "type": "mrkdwn",
              "text": "*Antall ganger påminnet:*\n${kommandokjede.sistSuspenderteSti.antallGangerPåminnet}"
            }
          ]
        }
        """.trimIndent()

    private fun link(query: String) =
        "https://logs.adeo.no/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-7d%2Fd,to:now))&_a=(columns:!(level,message,envclass,application,pod),filters:!(),hideChart:!f,index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:kuery,query:%22$query%22),sort:!(!('@timestamp',desc)))"
}
