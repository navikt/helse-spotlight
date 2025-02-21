package no.nav.helse.spotlight.db

import kotliquery.Row
import no.nav.helse.spotlight.Kommandokjede
import java.util.*

class KommandokjederDao(private val runner: SqlRunner) {
    fun lagre(kommandokjede: Kommandokjede) {
        if (finn(kommandokjede.commandContextId) == null) {
            insert(kommandokjede)
        } else {
            update(kommandokjede)
        }
    }

    private fun insert(kommandokjede: Kommandokjede) {
        runner.update(
            """
                insert into kommandokjeder (command_context_id, melding_id, command, sti, opprettet, antall_ganger_påminnet, tilstand)
                values (:command_context_id, :melding_id, :command, ${kommandokjede.sti.tilDatabaseArray()}, :opprettet, :antall_ganger_paaminnet, :tilstand) 
            """.trimIndent(),
            kommandokjede.tilParameterMap()
        )
    }

    fun finnAlle(): List<Kommandokjede> =
        runner.queryList("select * from kommandokjeder") { it.tilKommandokjede() }

    fun finn(commandContextId: UUID): Kommandokjede? =
        runner.querySingle(
            "select * from kommandokjeder where command_context_id = :command_context_id::uuid",
            mapOf("command_context_id" to commandContextId.toString())
        ) { it.tilKommandokjede() }

    fun finnAlleEldreEnnEnHalvtime() =
        runner.queryList(
            "select * from kommandokjeder where opprettet < current_timestamp - interval '30 minutes'",
        ) { it.tilKommandokjede() }

    private fun update(kommandokjede: Kommandokjede) {
        runner.update(
            """
                update kommandokjeder 
                set melding_id = :melding_id
                , command = :command
                , sti = ${kommandokjede.sti.tilDatabaseArray()}
                , opprettet = :opprettet
                , antall_ganger_påminnet = :antall_ganger_paaminnet
                , tilstand = :tilstand
                where command_context_id = :command_context_id::uuid
            """.trimIndent(),
            kommandokjede.tilParameterMap()
        )
    }

    fun slett(commandContextId: UUID) {
        runner.update(
            "delete from kommandokjeder where command_context_id = :command_context_id::uuid",
            mapOf("command_context_id" to commandContextId.toString())
        )
    }

    private fun List<Int>.tilDatabaseArray() = "'{${joinToString { " $it " }}}'"

    private fun Kommandokjede.tilParameterMap(): Map<String, Any?> =
        mapOf(
            "command_context_id" to commandContextId,
            "melding_id" to meldingId,
            "command" to command,
            "opprettet" to opprettet,
            "antall_ganger_paaminnet" to antallGangerPåminnet,
            "tilstand" to "SUSPENDERT",
        )

    private fun Row.tilKommandokjede() =
        Kommandokjede(
            commandContextId = uuid("command_context_id"),
            meldingId = uuid("melding_id"),
            command = string("command"),
            sti = array<Int>("sti").toList(),
            opprettet = localDateTime("opprettet"),
            antallGangerPåminnet = int("antall_ganger_påminnet"),
        )
}
