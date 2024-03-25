package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeFraDatabase
import org.intellij.lang.annotations.Language
import java.time.format.DateTimeFormatter

internal object SlackMeldingBuilder {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    internal fun List<KommandokjedeFraDatabase>.byggDagligSlackMelding(totaltAntall: Int? = null): String =
        attachments(buildSections(this), lagDagligMeldingTittel(totaltAntall))

    internal fun List<KommandokjedeFraDatabase>.byggPåminnelseSlackMelding(totaltAntall: Int? = null): String =
        attachments(buildSections(this), lagPåminnelseMeldingTittel(totaltAntall))

    private fun buildSections(kommandokjeder: List<KommandokjedeFraDatabase>): String {
        val iterator = kommandokjeder.iterator()
        return buildString {
            iterator.forEach {
                append(section(it))
                if (iterator.hasNext()) append(",")
            }
        }
    }

    @Language("JSON")
    private fun attachments(sections: String, tittel: String): String = """
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

    private fun lagPåminnelseMeldingTittel(antall: Int?): String =
        if (antall != null) {
            ":spotlight::warning: Det er $antall ${if (antall == 1) "kommandokjede" else "kommandokjeder"} med tilstand FEIL som har blitt påminnet: :warning::spotlight:"
        } else {
            ":the-more-you-know: Fortsettelse: :the-more-you-know:"
        }

    @Language("JSON")
    private fun section(kommandokjede: KommandokjedeFraDatabase) = """
        {
          "type": "section",
          "fields": [
            {
              "type": "mrkdwn",
              "text": "*Command context id:*\n<${link(kommandokjede.commandContextId.toString())}|${kommandokjede.commandContextId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Melding id:*\n<${link(kommandokjede.meldingId.toString())}|${kommandokjede.meldingId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Command:*\n${kommandokjede.command}"
            },
            {
              "type": "mrkdwn",
              "text": "*Sti:*\n${kommandokjede.sti}"
            },
            {
              "type": "mrkdwn",
              "text": "*Tidspunkt:*\n${kommandokjede.opprettet.format(formatter)}"
            },
            {
              "type": "mrkdwn",
              "text": "*Antall ganger påminnet:*\n${kommandokjede.antallGangerPåminnet}"
            }
          ]
        }
    """.trimIndent()

    private fun link(query: String) =
        "https://logs.adeo.no/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-7d%2Fd,to:now))&_a=(columns:!(level,message,envclass,application,pod),filters:!(),hideChart:!f,index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:kuery,query:%22$query%22),sort:!(!('@timestamp',desc)))"

}