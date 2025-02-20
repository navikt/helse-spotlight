package no.nav.helse.kafka.message

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.kafka.asUUID
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
