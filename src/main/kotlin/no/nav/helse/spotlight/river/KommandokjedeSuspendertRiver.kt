package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDateTime
import no.nav.helse.spotlight.Kommandokjede
import no.nav.helse.spotlight.db.TransactionManager
import java.util.*

class KommandokjedeSuspendertRiver(
    private val transactionManager: TransactionManager,
) : AbstractSimpleRiver("kommandokjede_suspendert") {
    override fun validate(message: JsonMessage) {
        message.rejectValue("command", "OppdaterPersonsnapshotCommand")
        message.requireKey("commandContextId")
        message.requireKey("meldingId")
        message.requireKey("command")
        message.requireKey("sti")
        message.requireKey("@opprettet")
    }

    override fun håndter(message: JsonMessage) {
        val kommandokjede = Kommandokjede(
            commandContextId = UUID.fromString(message["commandContextId"].asText()),
            meldingId = UUID.fromString(message["meldingId"].asText()),
            command = message["command"].asText(),
            sti = message["sti"].map { it.asInt() },
            opprettet = message["@opprettet"].asLocalDateTime(),
            antallGangerPåminnet = 0
        )
        transactionManager.transaction { dao ->
            dao.lagre(kommandokjede)
        }
    }
}
