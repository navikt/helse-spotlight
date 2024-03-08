package no.nav.helse

import com.zaxxer.hikari.HikariDataSource
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.kafka.*
import no.nav.helse.kafka.KommandokjedeFerdigstiltMessage.Companion.tilDatabase
import no.nav.helse.kafka.KommandokjedeSuspendertMessage.Companion.tilDatabase
import no.nav.helse.rapids_rivers.RapidsConnection
import no.nav.helse.slack.SlackClient
import no.nav.helse.slack.SlackMessageBuilder.byggSlackMelding

class Mediator(
    rapidsConnection: RapidsConnection,
    dataSource: HikariDataSource,
    private val slackClient: SlackClient,
    private val kommandokjedeDao: KommandokjedeDao = KommandokjedeDao(dataSource)
) {

    init {
        KommandokjedeFerdigstiltRiver(rapidsConnection, this)
        KommandokjedeSuspendertRiver(rapidsConnection, this)
        HelTimeRiver(rapidsConnection, this)
    }

    internal fun kommandokjedeFerdigstilt(message: KommandokjedeFerdigstiltMessage) {
        kommandokjedeDao.ferdigstilt(message.tilDatabase())
    }

    internal fun kommandokjedeSuspendert(message: KommandokjedeSuspendertMessage) {
        kommandokjedeDao.lagreSuspendert(message.tilDatabase())
    }

    internal fun fortellOmSuspenderteKommandokjeder() {
        val kommandokjederSomIkkeBleFerdigstilt = kommandokjedeDao.hentSuspenderteKommandokjeder()
        if (kommandokjederSomIkkeBleFerdigstilt.isEmpty()) {
            slackClient.postMessage(text = ":spotlight: Ingen kommandokjeder sitter fast :spotlight:")
        } else {
            slackClient.postMessage(attachments = kommandokjederSomIkkeBleFerdigstilt.byggSlackMelding())
        }
    }

}