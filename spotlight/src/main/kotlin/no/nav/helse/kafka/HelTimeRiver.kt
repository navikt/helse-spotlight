package no.nav.helse.kafka

import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.slf4j.LoggerFactory

internal class HelTimeRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAny("@event_name", listOf("hel_time", "post_suspenderte_kommandokjeder_til_slack"))
                it.demandValue("time", 6)
                it.demandAny("ukedag", listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        logg.info("KLOKKA ER 6 🐔")
    }

    companion object {
        private val logg = LoggerFactory.getLogger(KommandokjedeFerdigstiltRiver::class.java)
    }
}