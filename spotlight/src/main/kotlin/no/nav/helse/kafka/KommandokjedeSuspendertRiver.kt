package no.nav.helse.kafka

import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.MessageContext
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.rapids_rivers.River
import org.slf4j.LoggerFactory

internal class KommandokjedeSuspendertRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", "kommandokjede_suspendert")
                it.requireKey("commandContextId")
                it.requireKey("meldingId")
                it.requireKey("command")
                it.requireKey("sti")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }
    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        sikkerlogg.info("Leser melding ${packet.toJson()}")
        mediator.kommandokjedeSuspendert(KommandokjedeSuspendertMessage(packet))
    }

    companion object {
        private val logg = LoggerFactory.getLogger(KommandokjedeSuspendertRiver::class.java)
        private val sikkerlogg = LoggerFactory.getLogger("tjenestekall")
    }
}