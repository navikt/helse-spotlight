package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*

internal data class KommandokjedeFraDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val opprettet: LocalDateTime,
    val antallGangerPåminnet: Int,
    val tilstand: Tilstand,
)
