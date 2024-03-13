package no.nav.helse

import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import java.time.LocalDateTime
import java.util.*

object Testdata {

    internal fun kommandokjedeSuspendertFraDatabase() = KommandokjedeSuspendertFraDatabase(
        commandContextId = UUID.randomUUID(),
        meldingId = UUID.randomUUID(),
        command = "EnCommand",
        sti = listOf(1, 3),
        opprettet = LocalDateTime.now(),
        antallGangerPåminnet = 0
    )

    internal fun kommandokjedeSuspendertTilDatabase(
        commandContextId: UUID = UUID.randomUUID(),
        meldingId: UUID = UUID.randomUUID(),
        command: String = "EnCommand",
        opprettet: LocalDateTime = LocalDateTime.now(),
    ) = KommandokjedeSuspendertTilDatabase(
        commandContextId = commandContextId,
        meldingId = meldingId,
        command = command,
        sti = listOf(1, 3),
        opprettet = opprettet,
    )

    internal fun kommandokjedeSuspendertForOverEnHalvtimeSiden(
        commandContextId: UUID = UUID.randomUUID(),
        meldingId: UUID = UUID.randomUUID(),
    ) = kommandokjedeSuspendertTilDatabase(
        commandContextId = commandContextId,
        meldingId = meldingId,
        opprettet = LocalDateTime.now().minusMinutes(35)
    )

    internal fun kommandokjedeFerdigstilt(commandContextId: UUID) =
        KommandokjedeFerdigstiltTilDatabase(
            commandContextId = commandContextId,
            meldingId = UUID.randomUUID(),
            command = "EnCommand",
            opprettet = LocalDateTime.now()
        )

    internal fun kommandokjedeAvbrutt(commandContextId: UUID) =
        KommandokjedeAvbruttTilDatabase(
            commandContextId = commandContextId,
            meldingId = UUID.randomUUID(),
        )
}