package no.nav.helse.slack

import no.nav.helse.db.KommandokjedeSuspendertDto
import org.intellij.lang.annotations.Language

object SlackMessageBuilder {
    fun List<KommandokjedeSuspendertDto>.byggSlackMelding(): String =
        attachments(buildSections(this))

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
    private fun attachments(sections: String): String = """
        [
          {
            "color": "#36b528",
            "blocks": [
              {
                "type": "section",
                "text": {
                  "type": "mrkdwn",
                  "text": ":spotlight: Disse kommandokjedene sitter fast: :spotlight:"
                }
              },
              $sections
            ]
          }
        ]
    """.trimIndent()

    @Language("JSON")
    private fun section(kommandokjedeSuspendertDto: KommandokjedeSuspendertDto) = """
        {
          "type": "section",
          "fields": [
            {
              "type": "mrkdwn",
              "text": "*Command context id:*\n<linktilloggsøk.no|${kommandokjedeSuspendertDto.commandContextId}>"
            },
            {
              "type": "mrkdwn",
              "text": "*Melding id:*\n<linktilloggsøk.no|${kommandokjedeSuspendertDto.meldingId}>"
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
              "text": "*Tidspunkt:*\n${kommandokjedeSuspendertDto.opprettet}"
            }
          ]
        }
    """.trimIndent()
}