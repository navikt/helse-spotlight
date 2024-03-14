package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

internal class Meldingssender(private val rapidsConnection: RapidsConnection) {

    internal fun påminnSuspenderteKommandokjeder(
        kommandokjederSomSkalPåminnes: List<KommandokjedeSuspendertFraDatabase>
    ): List<KommandokjedeSuspendertFraDatabase> =
        kommandokjederSomSkalPåminnes.onEach { (commandContextId, meldingId) ->
            rapidsConnection.publish(
                JsonMessage.newMessage(
                    "kommandokjede_påminnelse",
                    mapOf(
                        "commandContextId" to commandContextId,
                        "meldingId" to meldingId
                    )
                ).toJson()
            )
        }

}