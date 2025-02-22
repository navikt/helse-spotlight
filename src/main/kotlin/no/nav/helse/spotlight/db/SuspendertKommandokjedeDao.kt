package no.nav.helse.spotlight.db

import kotliquery.Row
import no.nav.helse.spotlight.SuspendertKommandokjede
import java.util.*

class SuspendertKommandokjedeDao(private val runner: SqlRunner) {
    fun insert(kommandokjede: SuspendertKommandokjede) {
        runner.update(
            """
            INSERT INTO suspendert_kommandokjede (
              command_context_id,
              command,
              første_tidspunkt,
              siste_tidspunkt,
              siste_melding_id,
              totalt_antall_ganger_påminnet,
              sist_suspenderte_sti,
              sist_suspenderte_sti_første_tidspunkt,
              sist_suspenderte_sti_antall_ganger_påminnet
            )
            VALUES (
              :command_context_id,
              :command,
              :foerste_tidspunkt,
              :siste_tidspunkt,
              :siste_melding_id,
              :totalt_antall_ganger_paaminnet,
              :sist_suspenderte_sti,
              :sist_suspenderte_sti_foerste_tidspunkt,
              :sist_suspenderte_sti_antall_ganger_paaminnet
            ) 
            """.trimIndent(),
            kommandokjede.tilParameterMap(),
        )
    }

    fun finnAlle(): List<SuspendertKommandokjede> = runner.queryList("SELECT * FROM suspendert_kommandokjede") { it.tilKommandokjede() }

    fun finn(commandContextId: UUID): SuspendertKommandokjede? =
        runner.querySingle(
            "SELECT * FROM suspendert_kommandokjede WHERE command_context_id = :command_context_id",
            mapOf("command_context_id" to commandContextId),
        ) { it.tilKommandokjede() }

    fun finnAlleEldreEnnEnHalvtime() =
        runner.queryList(
            "SELECT * FROM suspendert_kommandokjede WHERE sist_suspenderte_sti_første_tidspunkt < CURRENT_TIMESTAMP - INTERVAL '30 minutes'",
        ) { it.tilKommandokjede() }

    fun update(kommandokjede: SuspendertKommandokjede) {
        runner.update(
            """
            UPDATE suspendert_kommandokjede SET
              command = :command,
              første_tidspunkt = :foerste_tidspunkt,
              siste_tidspunkt = :siste_tidspunkt,
              siste_melding_id = :siste_melding_id,
              totalt_antall_ganger_påminnet = :totalt_antall_ganger_paaminnet,
              sist_suspenderte_sti = :sist_suspenderte_sti,
              sist_suspenderte_sti_første_tidspunkt = :sist_suspenderte_sti_foerste_tidspunkt,
              sist_suspenderte_sti_antall_ganger_påminnet = :sist_suspenderte_sti_antall_ganger_paaminnet
            WHERE command_context_id = :command_context_id
            """.trimIndent(),
            kommandokjede.tilParameterMap(),
        )
    }

    fun slett(commandContextId: UUID) {
        runner.update(
            "DELETE FROM suspendert_kommandokjede WHERE command_context_id = :command_context_id",
            mapOf("command_context_id" to commandContextId),
        )
    }

    private fun SuspendertKommandokjede.tilParameterMap(): Map<String, Any?> =
        mapOf(
            "command_context_id" to commandContextId,
            "command" to command,
            "foerste_tidspunkt" to førsteTidspunkt,
            "siste_tidspunkt" to sisteTidspunkt,
            "siste_melding_id" to sisteMeldingId,
            "totalt_antall_ganger_paaminnet" to totaltAntallGangerPåminnet,
            "sist_suspenderte_sti" to sistSuspenderteSti.sti,
            "sist_suspenderte_sti_foerste_tidspunkt" to sistSuspenderteSti.førsteTidspunkt,
            "sist_suspenderte_sti_antall_ganger_paaminnet" to sistSuspenderteSti.antallGangerPåminnet,
        )

    private fun Row.tilKommandokjede() =
        SuspendertKommandokjede(
            commandContextId = uuid("command_context_id"),
            command = string("command"),
            førsteTidspunkt = instant("første_tidspunkt"),
            sisteTidspunkt = instant("siste_tidspunkt"),
            sisteMeldingId = uuid("siste_melding_id"),
            totaltAntallGangerPåminnet = int("totalt_antall_ganger_påminnet"),
            sistSuspenderteSti =
                SuspendertKommandokjede.Sti(
                    sti = string("sist_suspenderte_sti"),
                    førsteTidspunkt = instant("sist_suspenderte_sti_første_tidspunkt"),
                    antallGangerPåminnet = int("sist_suspenderte_sti_antall_ganger_påminnet"),
                ),
        )
}
