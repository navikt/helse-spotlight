package no.nav.helse.kafka.message

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDateTime
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import no.nav.helse.kafka.asUUID
import java.time.LocalDateTime
import java.util.*

internal class KommandokjedeSuspendertMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()
    private val command: String = packet["command"].asText()
    private val sti: List<Int> = packet["sti"].map { it.asInt() }
    private val opprettet: LocalDateTime = packet["@opprettet"].asLocalDateTime()

    internal companion object {
        internal fun KommandokjedeSuspendertMessage.tilDatabase() =
            KommandokjedeSuspendertTilDatabase(
                commandContextId = commandContextId,
                meldingId = meldingId,
                command = command,
                sti = sti,
                opprettet = opprettet
            )
    }

}
