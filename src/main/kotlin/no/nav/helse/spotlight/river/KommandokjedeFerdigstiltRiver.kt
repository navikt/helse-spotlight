package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import no.nav.helse.spotlight.db.TransactionManager
import java.util.*

class KommandokjedeFerdigstiltRiver(
    private val transactionManager: TransactionManager,
) : AbstractSimpleRiver("kommandokjede_ferdigstilt") {
    override fun validate(message: JsonMessage) {
        message.requireKey("commandContextId")
    }

    override fun håndter(message: JsonMessage) {
        val commandContextId = UUID.fromString(message["commandContextId"].asText())
        transactionManager.transaction { dao ->
            logg.info("Sletter ferdigstilt kommandokjede med commandContextId $commandContextId")
            dao.slett(commandContextId)
        }
    }
}
