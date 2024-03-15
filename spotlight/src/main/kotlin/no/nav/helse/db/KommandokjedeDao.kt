package no.nav.helse.db

import java.util.*
import javax.sql.DataSource

internal class KommandokjedeDao(dataSource: DataSource) : AbstractDao(dataSource) {

    internal fun upsert(kommandokjede: KommandokjedeSuspendertTilDatabase) {
        val stiForDatabase = kommandokjede.sti.joinToString { """ $it """ }
        query(
            """
                insert into suspenderte_kommandokjeder 
                values (:commandContextId, :meldingId, :command, '{$stiForDatabase}', :opprettet) 
                on conflict (command_context_id) do 
                update set melding_id = :meldingId, command = :command, sti = '{$stiForDatabase}', opprettet = :opprettet, antall_ganger_påminnet = 0
            """.trimIndent(),
            "commandContextId" to kommandokjede.commandContextId,
            "meldingId" to kommandokjede.meldingId,
            "command" to kommandokjede.command,
            "opprettet" to kommandokjede.opprettet,
        ).update()
    }

    internal fun ferdigstilt(kommandokjede: KommandokjedeFerdigstiltTilDatabase) = slett(kommandokjede.commandContextId)

    internal fun avbrutt(kommandokjede: KommandokjedeAvbruttTilDatabase) = slett(kommandokjede.commandContextId)

    internal fun hent() = query(
        "select * from suspenderte_kommandokjeder where opprettet < current_timestamp - interval '30 minutes'"
    ).list {
        KommandokjedeSuspendertFraDatabase(
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
            update suspenderte_kommandokjeder set antall_ganger_påminnet = antall_ganger_påminnet + 1 
            where command_context_id = :commandContextId
        """.trimIndent(), "commandContextId" to commandContextId
    ).update()

    private fun slett(commandContextId: UUID) = query(
        "delete from suspenderte_kommandokjeder where command_context_id = :commandContextId",
        "commandContextId" to commandContextId,
    ).update()

}