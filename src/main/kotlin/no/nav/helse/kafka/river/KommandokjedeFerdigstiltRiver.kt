package no.nav.helse.kafka.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageProblems
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.micrometer.core.instrument.MeterRegistry
import no.nav.helse.Mediator
import no.nav.helse.kafka.message.KommandokjedeFerdigstiltMessage
import no.nav.helse.kafka.message.KommandokjedeFerdigstiltMessage.Companion.tilDatabase
import org.slf4j.LoggerFactory

internal class KommandokjedeFerdigstiltRiver(rapidsConnection: RapidsConnection, private val mediator: Mediator): River.PacketListener {

    private companion object {
        private val logg = LoggerFactory.getLogger(this::class.java)
        private const val EVENT_NAME = "kommandokjede_ferdigstilt"
    }

    init {
        River(rapidsConnection).apply {
            validate {
                it.demandValue("@event_name", EVENT_NAME)
                it.requireKey("commandContextId")
                it.requireKey("meldingId")
                it.requireKey("command")
                it.requireKey("@opprettet")
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
        logg.info("Leser melding ${packet.toJson()}")
        mediator.kommandokjedeFerdigstilt(KommandokjedeFerdigstiltMessage(packet).tilDatabase())
    }

}
