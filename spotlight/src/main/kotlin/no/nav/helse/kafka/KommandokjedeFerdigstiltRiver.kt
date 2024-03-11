package no.nav.helse.kafka

import no.nav.helse.Mediator
import no.nav.helse.kafka.KommandokjedeFerdigstiltMessage.Companion.tilDatabase
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.slf4j.LoggerFactory

internal class KommandokjedeFerdigstiltRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    companion object {
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "kommandokjede_ferdigstilt")
                it.requireKey("commandContextId")
                it.requireKey("meldingId")
                it.requireKey("command")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        sikkerlogg.info("Leser melding ${packet.toJson()}")
        mediator.kommandokjedeFerdigstilt(KommandokjedeFerdigstiltMessage(packet).tilDatabase())
    }
}