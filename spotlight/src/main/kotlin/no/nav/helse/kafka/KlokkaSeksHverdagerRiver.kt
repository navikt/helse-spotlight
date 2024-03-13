package no.nav.helse.kafka

import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.*
import org.slf4j.LoggerFactory

internal class KlokkaSeksHverdagerRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_NAME = "hel_time"
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAny("@event_name", listOf(EVENT_NAME, "post_suspenderte_kommandokjeder_til_slack"))
                it.demandValue("time", 6)
                it.demandAny("ukedag", listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
            }
        }.register(this)
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        logg.error("Forstod ikke $EVENT_NAME:\n${problems.toExtendedReport()}")
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        logg.info("Klokka er 6 🐔. Forteller om suspenderte kommandokjeder på slack.")
        mediator.fortellOmSuspenderteKommandokjeder()
    }
}