package no.nav.helse.kafka

import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.RapidsConnection

internal class Meldingssender(private val rapidsConnection: RapidsConnection) {

    internal fun påminnSuspenderteKommandokjeder(
        kommandokjeder: List<KommandokjedeSuspendertFraDatabase>
    ): List<KommandokjedeSuspendertFraDatabase> =
        kommandokjeder.onEach { (commandContextId, meldingId) ->
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