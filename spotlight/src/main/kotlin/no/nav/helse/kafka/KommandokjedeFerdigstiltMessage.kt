package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeFerdigstiltDto
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.*

class KommandokjedeFerdigstiltMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()
    private val command: String = packet["command"].asText()
    private val opprettet: LocalDateTime = packet["@opprettet"].asLocalDateTime()

    companion object {
        internal fun KommandokjedeFerdigstiltMessage.tilDatabase() =
            KommandokjedeFerdigstiltDto(
                commandContextId = commandContextId,
                meldingId = meldingId,
                command = command,
                opprettet = opprettet
            )
    }
}