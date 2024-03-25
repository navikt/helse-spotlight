package no.nav.helse.kafka.message

import no.nav.helse.db.KommandokjedeFeiletTilDatabase
import no.nav.helse.kafka.asUUID
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.*

internal class KommandokjedeFeiletMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()
    private val command: String = packet["command"].asText()
    private val sti: List<Int> = packet["sti"].map { it.asInt() }
    private val opprettet: LocalDateTime = packet["@opprettet"].asLocalDateTime()

    internal companion object {
        internal fun KommandokjedeFeiletMessage.tilDatabase() =
            KommandokjedeFeiletTilDatabase(
                commandContextId = commandContextId,
                meldingId = meldingId,
                command = command,
                sti = sti,
                opprettet = opprettet
            )
    }

}