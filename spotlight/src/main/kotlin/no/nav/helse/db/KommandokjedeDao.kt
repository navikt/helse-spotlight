package no.nav.helse.db

import javax.sql.DataSource

class KommandokjedeDao(dataSource: DataSource): AbstractDao(dataSource) {
    fun lagreSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertForDatabase): Int {
        val stiForDatabase = kommandokjedeSuspendert.sti.joinToString { """ $it """ }
        return query(
            "insert into kommandokjede_ikke_ferdigstilt values (:commandContextId, :meldingId, :command, '{$stiForDatabase}', :opprettet)",
            "commandContextId" to kommandokjedeSuspendert.commandContextId,
            "meldingId" to kommandokjedeSuspendert.meldingId,
            "command" to kommandokjedeSuspendert.command,
            "opprettet" to kommandokjedeSuspendert.opprettet
        ).update()
    }

    fun ferdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltForDatabase) = query(
        "delete from kommandokjede_ikke_ferdigstilt where command_context_id = :commandContextId",
        "commandContextId" to kommandokjedeFerdigstilt.commandContextId,
    ).update()


}