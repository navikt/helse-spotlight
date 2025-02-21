package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.helse.spotlight.db.TransactionManager
import java.util.*

class KommandokjedeAvbruttRiver(
    private val transactionManager: TransactionManager,
) : AbstractSimpleRiver("kommandokjede_avbrutt") {
    override fun validate(message: JsonMessage) {
        message.requireKey("commandContextId")
    }

    override fun håndter(message: JsonMessage) {
        val commandContextId = UUID.fromString(message["commandContextId"].asText())
        transactionManager.transaction { dao ->
            dao.slett(commandContextId)
        }
    }
}
