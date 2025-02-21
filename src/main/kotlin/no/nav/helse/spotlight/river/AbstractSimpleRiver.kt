package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractSimpleRiver(
    private val eventName: String,
    private val altEventName: String? = null,
) : River.PacketListener {
    protected val logg: Logger = LoggerFactory.getLogger(this::class.java)

    protected open fun precondition(message: JsonMessage) {}

    protected open fun validate(message: JsonMessage) {}

    protected abstract fun håndter(message: JsonMessage)

    fun buildRiver(rapidsConnection: RapidsConnection) {
        River(rapidsConnection).apply {
            precondition {
                if (altEventName != null) {
                    it.requireAny("@event_name", listOf(eventName, altEventName))
                } else {
                    it.requireValue("@event_name", eventName)
                }
                precondition(it)
            }
            validate {
                validate(it)
            }
        }.register(this)
    }

    override fun onError(
        problems: MessageProblems,
        context: MessageContext,
        metadata: MessageMetadata,
    ) {
        logg.error("Forstod ikke ${altEventName?.let { "melding" } ?: eventName}:\n${problems.toExtendedReport()}")
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
        metadata: MessageMetadata,
        meterRegistry: MeterRegistry,
    ) {
        logg.info("Håndterer melding ${packet.toJson()}")
        håndter(packet)
    }
}
