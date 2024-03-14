package no.nav.helse.kafka.message

import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.kafka.asUUID
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.*

internal class KommandokjedeFerdigstiltMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()
    private val command: String = packet["command"].asText()
    private val opprettet: LocalDateTime = packet["@opprettet"].asLocalDateTime()

    internal companion object {
        internal fun KommandokjedeFerdigstiltMessage.tilDatabase() =
            KommandokjedeFerdigstiltTilDatabase(
                commandContextId = commandContextId,
                meldingId = meldingId,
                command = command,
                opprettet = opprettet
            )
    }

}