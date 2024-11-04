package no.nav.helse.db

import kotliquery.Row
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
    ).list { kommandokjedeFraDatabase(it) }

    internal fun påminnet(commandContextId: UUID): KommandokjedeFraDatabase = requireNotNull(query(
        """
            update kommandokjeder set antall_ganger_påminnet = antall_ganger_påminnet + 1 
            where command_context_id = :commandContextId
            returning *
        """.trimIndent(), "commandContextId" to commandContextId
    ).single { kommandokjedeFraDatabase(it) })

    private fun slett(commandContextId: UUID) = query(
        "delete from kommandokjeder where command_context_id = :commandContextId",
        "commandContextId" to commandContextId,
    ).update()

    private fun kommandokjedeFraDatabase(row: Row) =
        KommandokjedeFraDatabase(
            commandContextId = row.uuid("command_context_id"),
            meldingId = row.uuid("melding_id"),
            command = row.string("command"),
            sti = row.array<Int>("sti").toList(),
            opprettet = row.localDateTime("opprettet"),
            tilstand = enumValueOf(row.string("tilstand")),
            antallGangerPåminnet = row.int("antall_ganger_påminnet"),
        )

}