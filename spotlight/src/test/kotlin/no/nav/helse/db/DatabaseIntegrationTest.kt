package no.nav.helse.db

import kotliquery.Query
import kotliquery.Row
import kotliquery.action.QueryAction
import kotliquery.queryOf
import kotliquery.sessionOf
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

    protected fun assertPåminnet(commandContextId: UUID, forventetAntallGangerPåminnet: Int) {
        val antallGangerPåminnet = query(
            "select antall_ganger_påminnet from suspenderte_kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.int("antall_ganger_påminnet")
        }
        assertEquals(forventetAntallGangerPåminnet, antallGangerPåminnet)
    }

    protected fun assertLagret(commandContextId: UUID) {
        val kommandokjedeSuspendert = query(
            "select * from suspenderte_kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.uuid("command_context_id")
        }
        assertEquals(commandContextId, kommandokjedeSuspendert)
    }

    protected fun assertOppdatert(commandContextId: UUID, command: String) {
        val commandIDatabase = query(
            "select command from suspenderte_kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single {
            it.string("command")
        }
        assertEquals(commandIDatabase, command)
    }

    protected fun assertSlettet(commandContextId: UUID) {
        val antall = query(
            "select count(1) from suspenderte_kommandokjeder where command_context_id = :commandContextId",
            "commandContextId" to commandContextId
        ).single { it.int(1) }
        assertEquals(antall, 0)
    }
}