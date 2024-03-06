package no.nav.helse

import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.slf4j.LoggerFactory

internal class KommandokjedeFerdigstiltRiver(rapidsConnection: RapidsConnection): River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "kommandokjede_ferdigstilt")
                it.requireKey("commandContextId")
                it.requireKey("meldingId")
                it.requireKey("command")
                it.requireKey("@opprettet")
            }
        }
    }
    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        sikkerlogg.info("Leser melding ${packet.toJson()}")
    }

    companion object {
        private val logg = LoggerFactory.getLogger(KommandokjedeFerdigstiltRiver::class.java)
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    }
}