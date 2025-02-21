package no.nav.helse.spotlight.river

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import no.nav.helse.spotlight.Meldingsbygger
import no.nav.helse.spotlight.db.TransactionManager

class HverHalvtimeRiver(
    private val transactionManager: TransactionManager,
    private val rapidsConnection: RapidsConnection,
) : AbstractSimpleRiver("halv_time", "påminn_kommandokjeder_som_sitter_fast") {
    override fun håndter(message: JsonMessage) {
        logg.info("Påminner kommandokjeder som sitter fast")
        val kommandokjeder = transactionManager.transaction { dao ->
            dao.finnAlleEldreEnnEnHalvtime()
                .map { it.copy(antallGangerPåminnet = it.antallGangerPåminnet + 1) }
                .onEach(dao::lagre)
        }
        kommandokjeder.map(Meldingsbygger::byggKommandokjedePåminnelse).forEach(rapidsConnection::publish)
    }
}
