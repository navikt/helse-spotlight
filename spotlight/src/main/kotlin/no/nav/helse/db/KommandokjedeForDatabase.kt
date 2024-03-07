package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*

data class KommandokjedeSuspendertForDatabase(
    val commandoContext: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val opprettet: LocalDateTime
)

data class KommandokjedeFerdigstiltForDatabase(
    val commandoContext: UUID,
    val meldingId: UUID,
    val command: String,
    val opprettet: LocalDateTime
)