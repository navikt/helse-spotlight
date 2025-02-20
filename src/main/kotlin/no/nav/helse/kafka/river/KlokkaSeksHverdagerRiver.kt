package no.nav.helse.kafka.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.micrometer.core.instrument.MeterRegistry
import no.nav.helse.Mediator
import org.slf4j.LoggerFactory

internal class KlokkaSeksHverdagerRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_NAME = "hel_time"
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandAny("@event_name", listOf(EVENT_NAME, "post_kommandokjeder_til_slack"))
                it.demandValue("time", 6)
                it.demandAny("ukedag", listOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
            }
        }.register(this)
    }

    override fun onError(
        problems: MessageProblems,
        context: MessageContext,
        metadata: MessageMetadata,
    ) {
        logg.error("Forstod ikke $EVENT_NAME:\n${problems.toExtendedReport()}")
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
        metadata: MessageMetadata,
        meterRegistry: MeterRegistry,
    ) {
        logg.info("Klokka er 6 🐔. Forteller om kommandokjeder som sitter fast på slack.")
        mediator.fortellOmKommandokjeder()
    }

}
