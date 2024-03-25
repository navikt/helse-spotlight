package no.nav.helse

import no.nav.helse.db.*
import java.time.LocalDateTime
import java.util.*

internal object Testdata {

    internal val COMMAND_CONTEXT_ID: UUID = UUID.randomUUID()
    internal val MELDING_ID: UUID = UUID.randomUUID()
    internal val OPPRETTET: LocalDateTime = LocalDateTime.now()
    internal val STI: List<Int> = listOf(0)
    internal const val EN_COMMAND: String = "EnCommand"

    internal fun kommandokjedeFraDatabase(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        meldingId: UUID = MELDING_ID,
    ) = KommandokjedeFraDatabase(
        commandContextId = commandContextId,
        meldingId = meldingId,
        command = EN_COMMAND,
        sti = STI,
        opprettet = OPPRETTET,
        antallGangerPåminnet = 0
    )

    internal fun kommandokjedeSuspendertTilDatabase(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        command: String = EN_COMMAND,
        opprettet: LocalDateTime = OPPRETTET,
    ) = KommandokjedeSuspendertTilDatabase(
        commandContextId = commandContextId,
        meldingId = MELDING_ID,
        command = command,
        sti = STI,
        opprettet = opprettet,
    )

    internal fun kommandokjedeFeiletTilDatabase(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        command: String = EN_COMMAND,
        opprettet: LocalDateTime = OPPRETTET,
    ) = KommandokjedeFeiletTilDatabase(
        commandContextId = commandContextId,
        meldingId = MELDING_ID,
        command = command,
        sti = STI,
        opprettet = opprettet,
    )


    internal fun kommandokjedeSuspendertForOverEnHalvtimeSiden(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
    ) = kommandokjedeSuspendertTilDatabase(
        commandContextId = commandContextId,
        opprettet = OPPRETTET.minusMinutes(35)
    )

    internal fun kommandokjedeFeiletForOverEnHalvtimeSiden(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
    ) = kommandokjedeFeiletTilDatabase(
        commandContextId = commandContextId,
        opprettet = OPPRETTET.minusMinutes(35)
    )


    internal fun kommandokjedeFerdigstiltTilDatabase() =
        KommandokjedeFerdigstiltTilDatabase(
            commandContextId = COMMAND_CONTEXT_ID,
            meldingId = MELDING_ID,
            command = EN_COMMAND,
            opprettet = OPPRETTET,
        )

    internal fun kommandokjedeAvbruttTilDatabase() =
        KommandokjedeAvbruttTilDatabase(
            commandContextId = COMMAND_CONTEXT_ID,
            meldingId = MELDING_ID,
        )

}