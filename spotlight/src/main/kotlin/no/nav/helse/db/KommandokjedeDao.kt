package no.nav.helse.db

import java.util.*
import javax.sql.DataSource

class KommandokjedeDao(dataSource: DataSource): AbstractDao(dataSource) {

    fun lagreSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertTilDatabase): Int {
        val stiForDatabase = kommandokjedeSuspendert.sti.joinToString { """ $it """ }
        return query(
            """insert into kommandokjede_ikke_ferdigstilt 
               values (:commandContextId, :meldingId, :command, '{$stiForDatabase}', :opprettet)
               on conflict (command_context_id) do 
               update set melding_id = :meldingId, command = :command, sti = '{$stiForDatabase}', opprettet = :opprettet
            """.trimIndent(),
            "commandContextId" to kommandokjedeSuspendert.commandContextId,
            "meldingId" to kommandokjedeSuspendert.meldingId,
            "command" to kommandokjedeSuspendert.command,
            "opprettet" to kommandokjedeSuspendert.opprettet
        ).update()
    }

    fun ferdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltTilDatabase) = query(
        "delete from kommandokjede_ikke_ferdigstilt where command_context_id = :commandContextId",
        "commandContextId" to kommandokjedeFerdigstilt.commandContextId,
    ).update()

    fun hentSuspenderteKommandokjeder() = query(
        "select * from kommandokjede_ikke_ferdigstilt where opprettet < current_timestamp - interval '1 hour'"
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

    fun harBlittPåminnet(påminnetCommandContextId: UUID) = query(
        """
            update kommandokjede_ikke_ferdigstilt set antall_ganger_påminnet = antall_ganger_påminnet + 1 
            where command_context_id = :commandContextId
        """.trimIndent(),
        "commandContextId" to påminnetCommandContextId
    ).update()
}