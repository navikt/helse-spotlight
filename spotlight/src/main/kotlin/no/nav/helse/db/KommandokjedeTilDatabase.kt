package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*

data class KommandokjedeSuspendertTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val opprettet: LocalDateTime,
)

data class KommandokjedeFerdigstiltTilDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val opprettet: LocalDateTime,
)

