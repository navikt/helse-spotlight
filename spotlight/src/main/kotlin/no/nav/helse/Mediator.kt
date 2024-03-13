package no.nav.helse

import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import no.nav.helse.kafka.*
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.slack.SlackClient

class Mediator(
    private val rapidsConnection: RapidsConnection,
    private val slackClient: SlackClient,
    private val kommandokjedeDao: KommandokjedeDao,
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
        KommandokjedeAvbruttRiver(rapidsConnection, this)
        KlokkaSeksHverdagerRiver(rapidsConnection, this)
        HverHalvtimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeFerdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltTilDatabase) {
        kommandokjedeDao.ferdigstilt(kommandokjedeFerdigstilt)
    }

    internal fun kommandokjedeSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertTilDatabase) {
        kommandokjedeDao.lagreSuspendert(kommandokjedeSuspendert)
    }

    internal fun kommandokjedeAvbrutt(kommandokjedeAvbrutt: KommandokjedeAvbruttTilDatabase) {
        kommandokjedeDao.avbrutt(kommandokjedeAvbrutt)
    }

    internal fun fortellOmSuspenderteKommandokjeder() {
        slackClient.fortellOmSuspenderteKommandokjeder(kommandokjedeDao.hentSuspenderteKommandokjeder())
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