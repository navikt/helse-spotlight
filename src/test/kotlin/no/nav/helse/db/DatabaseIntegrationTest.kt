package no.nav.helse.db

import kotliquery.Query
import kotliquery.Row
import kotliquery.action.QueryAction
import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.helse.Testdata.COMMAND_CONTEXT_ID
import org.intellij.lang.annotations.Language
import java.util.*
import kotlin.test.assertEquals

internal abstract class DatabaseIntegrationTest: AbstractDatabaseTest() {

    internal val kommandokjedeDao = KommandokjedeDao(dataSource)

    protected fun query(@Language("postgresql") query: String, vararg params: Pair<String, Any?>) = queryOf(query, params.toMap())
    protected fun <T> Query.single(mapper: (Row) -> T?) = map(mapper).asSingle.runInSession()
    protected fun <T> Query.list(mapper: (Row) -> T?) = map(mapper).asList.runInSession()
    protected fun Query.update() = asUpdate.runInSession()
    protected fun Query.execute() = asExecute.runInSession()
    private fun <T> QueryAction<T>.runInSession() = sessionOf(dataSource).use(::runWithSession)

    protected fun assertPåminnet(commandContextId: UUID = COMMAND_CONTEXT_ID, forventetAntallGangerPåminnet: Int) {
        val antallGangerPåminnet = query(
            "select antall_ganger_påminnet from kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.int("antall_ganger_påminnet")
        }
        assertEquals(forventetAntallGangerPåminnet, antallGangerPåminnet)
    }

    protected fun assertLagret(commandContextId: UUID = COMMAND_CONTEXT_ID) {
        val kommandokjede = query(
            "select * from kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.uuid("command_context_id")
        }
        assertEquals(commandContextId, kommandokjede)
    }

    protected fun assertTilstand(commandContextId: UUID = COMMAND_CONTEXT_ID, tilstand: Tilstand) {
        val kommandokjedeTilstand = query(
            "select * from kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.string("tilstand")
        }
        assertEquals(tilstand.name, kommandokjedeTilstand)
    }


    protected fun assertOppdatert(commandContextId: UUID = COMMAND_CONTEXT_ID, command: String) {
        val commandIDatabase = query(
            "select command from kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.string("command")
        }
        assertEquals(commandIDatabase, command)
    }

    protected fun assertSlettet(commandContextId: UUID = COMMAND_CONTEXT_ID) {
        val antall = query(
            "select count(1) from kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single { it.int(1) }
        assertEquals(antall, 0)
    }

}
