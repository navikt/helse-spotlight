package no.nav.helse.kafka.message

import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.kafka.asUUID
import no.nav.helse.rapids_rivers.JsonMessage
import java.util.*

internal class KommandokjedeAvbruttMessage(packet: JsonMessage) {

    private val commandContextId: UUID = packet["commandContextId"].asUUID()
    private val meldingId: UUID = packet["meldingId"].asUUID()

    internal companion object {
        internal fun KommandokjedeAvbruttMessage.tilDatabase() =
            KommandokjedeAvbruttTilDatabase(
                commandContextId = commandContextId,
                meldingId = meldingId,
            )
    }
}