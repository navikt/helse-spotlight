package no.nav.helse.db

import no.nav.helse.db.Tilstand.SUSPENDERT
import java.time.LocalDateTime
import java.util.*

internal enum class Tilstand {
    SUSPENDERT
}

internal data class KommandokjedeSuspendertTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val tilstand: Tilstand = SUSPENDERT,
    val opprettet: LocalDateTime,
)

internal data class KommandokjedeFerdigstiltTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val opprettet: LocalDateTime,
)

internal data class KommandokjedeAvbruttTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
)

