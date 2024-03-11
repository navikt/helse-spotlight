package no.nav.helse

import no.nav.helse.db.KommandokjedeFerdigstiltDto
import no.nav.helse.db.KommandokjedeSuspendertDto
import java.time.LocalDateTime
import java.util.*

object Testdata {

    internal fun kommandokjedeSuspendert(
        commandContextId: UUID = UUID.randomUUID(),
        command: String = "EnCommand",
        opprettet: LocalDateTime = LocalDateTime.now(),
    ) = KommandokjedeSuspendertDto(
        commandContextId = commandContextId,
        meldingId = UUID.randomUUID(),
        command = command,
        sti = listOf(1, 3),
        opprettet = opprettet,
    )

    internal fun kommandokjedeSuspendertForOverEnTimeSiden(
        commandContextId: UUID = UUID.randomUUID(),
    ) = kommandokjedeSuspendert(commandContextId = commandContextId, opprettet = LocalDateTime.now().minusHours(2))

    internal fun kommandokjedeFerdigstilt(commandContextId: UUID) =
        KommandokjedeFerdigstiltDto(
            commandContextId = commandContextId,
            meldingId = UUID.randomUUID(),
            command = "EnCommand",
            opprettet = LocalDateTime.now()
        )
}