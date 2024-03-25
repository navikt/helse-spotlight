package no.nav.helse.kafka.river

import no.nav.helse.Mediator
import no.nav.helse.rapids_rivers.*
import org.slf4j.LoggerFactory

internal class HverHalvtimeRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_NAME = "halv_time"
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAny("@event_name", listOf(EVENT_NAME, "påminn_kommandokjeder_som_sitter_fast"))
            }
        }.register(this)
    }

    override fun onError(problems: MessageProblems, context: MessageContext) {
        logg.error("Forstod ikke $EVENT_NAME:\n${problems.toExtendedReport()}")
    }

    override fun onPacket(packet: JsonMessage, context: MessageContext) {
        logg.info("Påminner kommandokjeder som sitter fast")
        mediator.påminnKommandokjeder()
    }

}