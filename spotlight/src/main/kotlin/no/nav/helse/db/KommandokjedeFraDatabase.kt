package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*

data class KommandokjedeSuspendertFraDatabase(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val opprettet: LocalDateTime,
    val antallGangerPåminnet: Int,
)
