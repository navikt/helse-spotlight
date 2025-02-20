package no.nav.helse

import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.kafka.river.*
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
        KlokkaSeksHverdagerRiver(rapidsConnection, this)
        HverHalvtimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeSuspendert(kommandokjede: KommandokjedeSuspendertTilDatabase) =
        kommandokjedeDao.upsert(kommandokjede)

    internal fun kommandokjedeFerdigstilt(kommandokjede: KommandokjedeFerdigstiltTilDatabase) =
        kommandokjedeDao.ferdigstilt(kommandokjede)

    internal fun kommandokjedeAvbrutt(kommandokjede: KommandokjedeAvbruttTilDatabase) =
        kommandokjedeDao.avbrutt(kommandokjede)

    internal fun fortellOmKommandokjeder() =
        slackClient.fortellOmKommandokjeder(kommandokjederSomErPåminnet())

    internal fun påminnKommandokjeder() =
        meldingssender.påminnKommandokjeder(kommandokjeder())
            .map { (commandContextId) -> commandContextId.påminnet() }

    private fun kommandokjeder() = kommandokjedeDao.hent()
    private fun kommandokjederSomErPåminnet() = kommandokjeder().filter { it.antallGangerPåminnet > 0 }
    private fun UUID.påminnet() = kommandokjedeDao.påminnet(this)

}
