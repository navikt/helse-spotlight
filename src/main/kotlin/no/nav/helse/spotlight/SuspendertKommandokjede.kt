package no.nav.helse.spotlight

import java.time.Instant
import java.util.*

data class SuspendertKommandokjede(
    val partisjonsnøkkel: String?,
    val commandContextId: UUID,
    val command: String,
    val førsteTidspunkt: Instant,
    val sisteTidspunkt: Instant,
    val sisteMeldingId: UUID,
    val totaltAntallGangerPåminnet: Int,
    val sistSuspenderteSti: Sti,
) {
    data class Sti(
        val sti: String,
        val førsteTidspunkt: Instant,
        val antallGangerPåminnet: Int,
    )
}
