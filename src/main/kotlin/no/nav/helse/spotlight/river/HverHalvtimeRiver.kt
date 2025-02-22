package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.spotlight.Meldingsbygger
import no.nav.helse.spotlight.SuspendertKommandokjede
import no.nav.helse.spotlight.db.TransactionManager

class HverHalvtimeRiver(
    private val transactionManager: TransactionManager,
    private val rapidsConnection: RapidsConnection,
) : AbstractSimpleRiver("halv_time", "påminn_kommandokjeder_som_sitter_fast") {
    override fun håndter(message: JsonMessage) {
        logg.info("Påminner kommandokjeder som sitter fast")
        val kommandokjeder =
            transactionManager.transaction { dao ->
                dao.finnAlleEldreEnnEnHalvtime()
                    .map { it.medØktAntallGangerPåminnet() }
                    .onEach(dao::update)
            }
        kommandokjeder.map(Meldingsbygger::byggKommandokjedePåminnelse).forEach(rapidsConnection::publish)
    }
}

private fun SuspendertKommandokjede.medØktAntallGangerPåminnet(): SuspendertKommandokjede =
    copy(
        totaltAntallGangerPåminnet = totaltAntallGangerPåminnet + 1,
        sistSuspenderteSti =
            sistSuspenderteSti.copy(
                antallGangerPåminnet = sistSuspenderteSti.antallGangerPåminnet + 1,
            ),
    )
