package no.nav.helse.kafka.river

import no.nav.helse.Mediator
import no.nav.helse.kafka.message.KommandokjedeSuspendertMessage
import no.nav.helse.kafka.message.KommandokjedeSuspendertMessage.Companion.tilDatabase
import no.nav.helse.rapids_rivers.*
import org.slf4j.LoggerFactory

internal class KommandokjedeSuspendertRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator) : River.PacketListener {
    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_NAME = "kommandokjede_suspendert"
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", EVENT_NAME)
                it.rejectValue("command", "OppdaterPersonsnapshotCommand")
                it.requireKey("commandContextId")
                it.requireKey("meldingId")
                it.requireKey("command")
                it.requireKey("sti")
                it.requireKey("@opprettet")
            }
        }.register(this)
    }

    override fun onError(
        problems: MessageProblems,
        context: MessageContext,
    ) {
        logg.error("Forstod ikke $EVENT_NAME:\n${problems.toExtendedReport()}")
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
    ) {
        logg.info("Leser melding ${packet.toJson()}")
        mediator.kommandokjedeSuspendert(KommandokjedeSuspendertMessage(packet).tilDatabase())
    }
}
