package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.asLocalDateTime
import no.nav.helse.spotlight.SuspendertKommandokjede
import no.nav.helse.spotlight.db.TransactionManager
import java.time.Instant
import java.time.ZoneId
import java.util.*

class KommandokjedeSuspendertRiver(
    private val transactionManager: TransactionManager,
) : AbstractSimpleRiver("kommandokjede_suspendert") {
    override fun precondition(message: JsonMessage) {
        message.forbidValue("command", "OppdaterPersonsnapshotCommand")
    }

    override fun validate(message: JsonMessage) {
        message.requireKey("commandContextId")
        message.requireKey("meldingId")
        message.requireKey("command")
        message.requireKey("sti")
        message.requireKey("@opprettet")
    }

    override fun håndter(message: JsonMessage) {
        val commandContextId = UUID.fromString(message["commandContextId"].asText())
        val meldingId = UUID.fromString(message["meldingId"].asText())
        val command = message["command"].asText()
        val sti = message["sti"].toPrettyString()
        val opprettetTidspunkt = message["@opprettet"].asLocalDateTime().atZone(ZoneId.of("Europe/Oslo")).toInstant()

        transactionManager.transaction { dao ->
            val eksisterendeKommandokjede = dao.finn(commandContextId)
            if (eksisterendeKommandokjede == null) {
                dao.insert(
                    nyKommandokjede(
                        commandContextId = commandContextId,
                        command = command,
                        tidspunkt = opprettetTidspunkt,
                        meldingId = meldingId,
                        sti = sti,
                    ),
                )
            } else {
                dao.update(
                    eksisterendeKommandokjede.oppdatertMedNyMelding(
                        tidspunkt = opprettetTidspunkt,
                        meldingId = meldingId,
                        sti = sti,
                    ),
                )
            }
        }
    }

    private fun nyKommandokjede(
        commandContextId: UUID,
        command: String,
        tidspunkt: Instant,
        meldingId: UUID,
        sti: String,
    ) = SuspendertKommandokjede(
        commandContextId = commandContextId,
        command = command,
        førsteTidspunkt = tidspunkt,
        sisteTidspunkt = tidspunkt,
        sisteMeldingId = meldingId,
        totaltAntallGangerPåminnet = 0,
        sistSuspenderteSti =
            SuspendertKommandokjede.Sti(
                sti = sti,
                førsteTidspunkt = tidspunkt,
                antallGangerPåminnet = 0,
            ),
    )

    private fun SuspendertKommandokjede.oppdatertMedNyMelding(
        tidspunkt: Instant,
        meldingId: UUID,
        sti: String,
    ) = copy(
        sisteTidspunkt = tidspunkt,
        sisteMeldingId = meldingId,
        sistSuspenderteSti =
            sistSuspenderteSti.takeIf { it.sti == sti }
                ?: SuspendertKommandokjede.Sti(
                    sti = sti,
                    førsteTidspunkt = tidspunkt,
                    antallGangerPåminnet = 0,
                ),
    )
}
