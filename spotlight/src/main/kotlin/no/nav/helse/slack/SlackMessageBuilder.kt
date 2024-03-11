package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeSuspendertDto
import org.intellij.lang.annotations.Language
import java.time.format.DateTimeFormatter

object SlackMessageBuilder {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

    fun List<KommandokjedeSuspendertDto>.byggSlackMelding(totaltAntall: Int? = null): String =
        attachments(buildSections(this), totaltAntall)

    private fun buildSections(suspenderteKommandokjeder: List<KommandokjedeSuspendertDto>): String {
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
            ":spotlight: Det er $antall kommandokjeder som sitter fast: :spotlight:"
        } else {
            ":the-more-you-know: Forsettelse: :the-more-you-know:"
        }

    @Language("JSON")
    private fun section(kommandokjedeSuspendertDto: KommandokjedeSuspendertDto) = """
        {
          "type": "section",
          "fields": [
            {
              "type": "mrkdwn",
              "text": "*Command context id:*\n<${link(kommandokjedeSuspendertDto.commandContextId.toString())}|${kommandokjedeSuspendertDto.commandContextId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Melding id:*\n<${link(kommandokjedeSuspendertDto.meldingId.toString())}|${kommandokjedeSuspendertDto.meldingId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Command:*\n${kommandokjedeSuspendertDto.command}"
            },
            {
              "type": "mrkdwn",
              "text": "*Sti:*\n${kommandokjedeSuspendertDto.sti}"
            },
            {
              "type": "mrkdwn",
              "text": "*Tidspunkt:*\n${kommandokjedeSuspendertDto.opprettet.format(formatter)}"
            }
          ]
        }
    """.trimIndent()

    private fun link(query: String) =
        "https://logs.adeo.no/app/discover#/?_g=(filters:!(),refreshInterval:(pause:!t,value:60000),time:(from:now-7d%2Fd,to:now))&_a=(columns:!(level,message,envclass,application,pod),filters:!(),hideChart:!f,index:'96e648c0-980a-11e9-830a-e17bbd64b4db',interval:auto,query:(language:kuery,query:%22$query%22),sort:!(!('@timestamp',desc)))"
}