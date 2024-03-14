package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

internal class Meldingssender(private val rapidsConnection: RapidsConnection, private val kommandokjedeDao: KommandokjedeDao) {

    internal fun påminnSuspenderteKommandokjeder(kommandokjederSomSkalPåminnes: List<KommandokjedeSuspendertFraDatabase>) {
        kommandokjederSomSkalPåminnes.forEach { kommandokjede ->
            rapidsConnection.publish(
                JsonMessage.newMessage(
                    "kommandokjede_påminnelse",
                    mapOf(
                        "commandContextId" to kommandokjede.commandContextId,
                        "meldingId" to kommandokjede.meldingId
                    )
                ).toJson()
            ).also {
                kommandokjedeDao.påminnet(kommandokjede.commandContextId)
            }
        }
    }

}