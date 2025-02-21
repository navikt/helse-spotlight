package no.nav.helse.spotlight

import java.time.LocalDateTime
import java.util.*

data class Kommandokjede(
    val commandContextId: UUID,
    val meldingId: UUID,
    val command: String,
    val sti: List<Int>,
    val opprettet: LocalDateTime,
    val antallGangerPåminnet: Int,
)
