package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*

internal data class KommandokjedeSuspendertTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
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

