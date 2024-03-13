package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

class Meldingssender(private val rapidsConnection: RapidsConnection, private val kommandokjedeDao: KommandokjedeDao) {

    fun påminnSuspenderteKommandokjeder(kommandokjederSomSkalPåminnes: List<KommandokjedeSuspendertFraDatabase>) {
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