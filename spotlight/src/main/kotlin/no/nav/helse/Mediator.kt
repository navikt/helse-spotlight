package no.nav.helse

import com.zaxxer.hikari.HikariDataSource
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import no.nav.helse.kafka.HverHalvtimeRiver
import no.nav.helse.kafka.KlokkaSeksHverdagerRiver
import no.nav.helse.kafka.KommandokjedeFerdigstiltRiver
import no.nav.helse.kafka.KommandokjedeSuspendertRiver
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.slack.SlackClient
import no.nav.helse.slack.SlackMessageBuilder.byggSlackMelding

class Mediator(
    private val rapidsConnection: RapidsConnection,
    dataSource: HikariDataSource,
    private val slackClient: SlackClient,
    private val kommandokjedeDao: KommandokjedeDao = KommandokjedeDao(dataSource)
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
        KlokkaSeksHverdagerRiver(rapidsConnection, this)
        HverHalvtimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeFerdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltTilDatabase) {
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstilt)
    }

    internal fun kommandokjedeSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertTilDatabase) {
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendert)
    }

    internal fun fortellOmSuspenderteKommandokjeder() {
        val kommandokjederSomIkkeBleFerdigstilt = kommandokjedeDao.hentSuspenderteKommandokjeder()
        if (kommandokjederSomIkkeBleFerdigstilt.isEmpty()) {
            slackClient.postMessage(text = ":spotlight: Ingen kommandokjeder sitter fast :spotlight:")
        } else {
            // Slack APIet støtter bare 50 blocks pr melding. Hvis det er mer enn 50 stuck kommandokjeder
            // postes resterende i tråd.
            var threadTs: String? = null
            kommandokjederSomIkkeBleFerdigstilt.chunked(49).forEach {
                if (threadTs == null) {
                    threadTs =
                        slackClient.postMessage(attachments = it.byggSlackMelding(kommandokjederSomIkkeBleFerdigstilt.size))
                } else {
                    slackClient.postMessage(attachments = it.byggSlackMelding(), threadTs = threadTs)
                }
            }
        }
    }

    internal fun påminnSuspenderteKommandokjeder() {
        val kommandokjederSomSkalPåminnes = kommandokjedeDao.hentSuspenderteKommandokjeder()
        kommandokjederSomSkalPåminnes.forEach { kommandokjedeSuspendertFraDatabase ->
            rapidsConnection.publish(
                JsonMessage.newMessage(
                    "kommandokjede_påminnelse",
                    mapOf(
                        "commandContextId" to kommandokjedeSuspendertFraDatabase.commandContextId,
                        "meldingId" to kommandokjedeSuspendertFraDatabase.meldingId
                    )
                ).toJson()
            ).also {
                kommandokjedeDao.harBlittPåminnet(kommandokjedeSuspendertFraDatabase.commandContextId)
            }
        }
    }
}