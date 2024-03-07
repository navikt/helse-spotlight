package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeSuspendertForDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.asLocalDateTime
import java.time.LocalDateTime
import java.util.*

class KommandokjedeSuspendertMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()
    private val command: String = packet["command"].asText()
    private val sti: List<Int> = packet["sti"].map { it.asInt() }
    private val opprettet: LocalDateTime = packet["@opprettet"].asLocalDateTime()

    companion object {
        internal fun KommandokjedeSuspendertMessage.tilDatabase() =
            KommandokjedeSuspendertForDatabase(
                commandoContext = commandContextId,
                meldingId = meldingId,
                command = command,
                sti = sti,
                opprettet = opprettet
            )
    }
}