package no.nav.helse

import no.nav.helse.db.*
import no.nav.helse.db.Tilstand.FEIL
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.kafka.river.*
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.slack.SlackClient
import java.util.*

internal class Mediator(
    rapidsConnection: RapidsConnection,
    private val slackClient: SlackClient,
    private val meldingssender: Meldingssender,
    private val kommandokjedeDao: KommandokjedeDao,
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
        KommandokjedeAvbruttRiver(rapidsConnection, this)
        KommandokjedeFeiletRiver(rapidsConnection, this)
        KlokkaSeksHverdagerRiver(rapidsConnection, this)
        HverHalvtimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeSuspendert(kommandokjede: KommandokjedeSuspendertTilDatabase) =
        kommandokjedeDao.upsert(kommandokjede)

    internal fun kommandokjedeFeilet(kommandokjede: KommandokjedeFeiletTilDatabase) =
        kommandokjedeDao.upsert(kommandokjede)

    internal fun kommandokjedeFerdigstilt(kommandokjede: KommandokjedeFerdigstiltTilDatabase) =
        kommandokjedeDao.ferdigstilt(kommandokjede)

    internal fun kommandokjedeAvbrutt(kommandokjede: KommandokjedeAvbruttTilDatabase) =
        kommandokjedeDao.avbrutt(kommandokjede)

    internal fun fortellOmKommandokjeder() =
        slackClient.fortellOmKommandokjeder(kommandokjeder())

    internal fun påminnKommandokjeder() =
        meldingssender.påminnKommandokjeder(kommandokjeder())
            .onEach { kommandokjede ->
                kommandokjede.commandContextId.påminnet()
            }
            .filter { it.tilstand == FEIL }
            .takeUnless { it.isEmpty() }
            ?.let { slackClient.fortellOmKommandokjederPåminnetMedTilstandFeil(it) }

    private fun kommandokjeder() = kommandokjedeDao.hent()
    private fun UUID.påminnet() = kommandokjedeDao.påminnet(this)

}