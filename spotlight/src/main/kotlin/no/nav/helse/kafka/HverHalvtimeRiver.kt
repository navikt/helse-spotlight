package no.nav.helse.kafka

import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.slf4j.LoggerFactory

internal class HverHalvtimeRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    companion object {
        private val logg = LoggerFactory.getLogger(HverHalvtimeRiver::class.java)
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAny("@event_name", listOf("halv_time", "suspenderte_kommandokjeder_påminnelse"))
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        logg.info("Påminner suspenderte kommandokjeder")
        mediator.påminnSuspenderteKommandokjeder()
    }
}