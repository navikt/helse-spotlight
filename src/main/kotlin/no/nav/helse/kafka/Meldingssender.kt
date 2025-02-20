package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.db.KommandokjedeFraDatabase

internal class Meldingssender(private val rapidsConnection: RapidsConnection) {

    internal fun påminnKommandokjeder(
        kommandokjeder: List<KommandokjedeFraDatabase>
    ): List<KommandokjedeFraDatabase> =
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
