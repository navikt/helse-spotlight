package no.nav.helse.db

import javax.sql.DataSource
import kotliquery.Query
import kotliquery.Row
import kotliquery.action.QueryAction
import kotliquery.queryOf
import kotliquery.sessionOf
import org.intellij.lang.annotations.Language

internal abstract class AbstractDao(private val dataSource: DataSource) {
    protected fun query(@Language("postgresql") query: String, vararg params: Pair<String, Any?>) = queryOf(query, params.toMap())
    protected fun <T> Query.single(mapper: (Row) -> T?) = map(mapper).asSingle.runInSession()
    protected fun <T> Query.list(mapper: (Row) -> T?) = map(mapper).asList.runInSession()
    protected fun Query.update() = asUpdate.runInSession()
    protected fun Query.execute() = asExecute.runInSession()
    private fun <T> QueryAction<T>.runInSession() = sessionOf(dataSource).use(::runWithSession)
}
