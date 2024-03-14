package no.nav.helse.db

import java.util.*
import javax.sql.DataSource

internal class KommandokjedeDao(dataSource: DataSource) : AbstractDao(dataSource) {

    internal fun lagreSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertTilDatabase): Int {
        val stiForDatabase = kommandokjedeSuspendert.sti.joinToString { """ $it """ }
        return query(
            """
                insert into suspenderte_kommandokjeder 
                values (:commandContextId, :meldingId, :command, '{$stiForDatabase}', :opprettet) 
                on conflict (command_context_id) do 
                update set melding_id = :meldingId, command = :command, sti = '{$stiForDatabase}', opprettet = :opprettet, antall_ganger_påminnet = 0
            """.trimIndent(),
            "commandContextId" to kommandokjedeSuspendert.commandContextId,
            "meldingId" to kommandokjedeSuspendert.meldingId,
            "command" to kommandokjedeSuspendert.command,
            "opprettet" to kommandokjedeSuspendert.opprettet,
        ).update()
    }

    internal fun ferdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltTilDatabase) =
        slett(kommandokjedeFerdigstilt.commandContextId)

    internal fun avbrutt(kommandokjedeAvbrutt: KommandokjedeAvbruttTilDatabase) = slett(kommandokjedeAvbrutt.commandContextId)

    internal fun hentSuspenderteKommandokjeder() = query(
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

    internal fun harBlittPåminnet(påminnetCommandContextId: UUID) = query(
        """
            update suspenderte_kommandokjeder set antall_ganger_påminnet = antall_ganger_påminnet + 1 
            where command_context_id = :commandContextId
        """.trimIndent(),
        "commandContextId" to påminnetCommandContextId
    ).update()

    private fun slett(commandContextId: UUID) = query(
        "delete from suspenderte_kommandokjeder where command_context_id = :commandContextId",
        "commandContextId" to commandContextId,
    ).update()
}