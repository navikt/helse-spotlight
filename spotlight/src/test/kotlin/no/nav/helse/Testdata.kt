package no.nav.helse

import no.nav.helse.db.KommandokjedeAvbruttTilDatabase
import no.nav.helse.db.KommandokjedeFerdigstiltTilDatabase
import no.nav.helse.db.KommandokjedeSuspendertFraDatabase
import no.nav.helse.db.KommandokjedeSuspendertTilDatabase
import java.time.LocalDateTime
import java.util.*

internal object Testdata {

    internal val COMMAND_CONTEXT_ID: UUID = UUID.randomUUID()

    internal fun kommandokjedeSuspendertFraDatabase(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        meldingId: UUID = UUID.randomUUID()
    ) = KommandokjedeSuspendertFraDatabase(
        commandContextId = commandContextId,
        meldingId = meldingId,
        command = "EnCommand",
        sti = listOf(1, 3),
        opprettet = LocalDateTime.now(),
        antallGangerPåminnet = 0
    )

    internal fun kommandokjedeSuspendertTilDatabase(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
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
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        meldingId: UUID = UUID.randomUUID(),
    ) = kommandokjedeSuspendertTilDatabase(
        commandContextId = commandContextId,
        meldingId = meldingId,
        opprettet = LocalDateTime.now().minusMinutes(35)
    )

    internal fun kommandokjedeFerdigstiltTilDatabase(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        KommandokjedeFerdigstiltTilDatabase(
            commandContextId = commandContextId,
            meldingId = UUID.randomUUID(),
            command = "EnCommand",
            opprettet = LocalDateTime.now()
        )

    internal fun kommandokjedeAvbruttTilDatabase(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        KommandokjedeAvbruttTilDatabase(
            commandContextId = commandContextId,
            meldingId = UUID.randomUUID(),
        )

}