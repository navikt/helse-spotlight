package no.nav.helse.db

import java.time.LocalDateTime
import java.util.*
import javax.sql.DataSource

internal class KommandokjedeDao(dataSource: DataSource) : AbstractDao(dataSource) {

    internal fun upsert(kommandokjede: KommandokjedeSuspendertTilDatabase) = upsert(
        commandContextId = kommandokjede.commandContextId,
        meldingId = kommandokjede.meldingId,
        command = kommandokjede.command,
        sti = kommandokjede.sti,
        tilstand = kommandokjede.tilstand,
        opprettet = kommandokjede.opprettet
    )

    internal fun upsert(kommandokjede: KommandokjedeFeiletTilDatabase) = upsert(
        commandContextId = kommandokjede.commandContextId,
        meldingId = kommandokjede.meldingId,
        command = kommandokjede.command,
        sti = kommandokjede.sti,
        tilstand = kommandokjede.tilstand,
        opprettet = kommandokjede.opprettet
    )

    private fun upsert(
        commandContextId: UUID,
        meldingId: UUID,
        command: String,
        sti: List<Int>,
        tilstand: Tilstand,
        opprettet: LocalDateTime
    ) {
        val stiForDatabase = sti.joinToString { """ $it """ }
        query(
            """
                insert into kommandokjeder 
                values (:commandContextId, :meldingId, :command, '{$stiForDatabase}', :opprettet, 0, :tilstand) 
                on conflict (command_context_id) do 
                update set melding_id = :meldingId, command = :command, sti = '{$stiForDatabase}', opprettet = :opprettet, antall_ganger_påminnet = 0, tilstand = :tilstand
            """.trimIndent(),
            "commandContextId" to commandContextId,
            "meldingId" to meldingId,
            "command" to command,
            "tilstand" to tilstand.name,
            "opprettet" to opprettet,
        ).update()
    }

    internal fun ferdigstilt(kommandokjede: KommandokjedeFerdigstiltTilDatabase) = slett(kommandokjede.commandContextId)

    internal fun avbrutt(kommandokjede: KommandokjedeAvbruttTilDatabase) = slett(kommandokjede.commandContextId)

    internal fun hent() = query(
        "select * from kommandokjeder where opprettet < current_timestamp - interval '30 minutes'"
    ).list {
        KommandokjedeFraDatabase(
            commandContextId = it.uuid("command_context_id"),
            meldingId = it.uuid("melding_id"),
            command = it.string("command"),
            sti = it.array<Int>("sti").toList(),
            opprettet = it.localDateTime("opprettet"),
            antallGangerPåminnet = it.int("antall_ganger_påminnet")
        )
    }

    internal fun påminnet(commandContextId: UUID) = query(
        """
            update kommandokjeder set antall_ganger_påminnet = antall_ganger_påminnet + 1 
            where command_context_id = :commandContextId
        """.trimIndent(), "commandContextId" to commandContextId
    ).update()

    private fun slett(commandContextId: UUID) = query(
        "delete from kommandokjeder where command_context_id = :commandContextId",
        "commandContextId" to commandContextId,
    ).update()

}