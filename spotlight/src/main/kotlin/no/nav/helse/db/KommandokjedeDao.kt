package no.nav.helse.db

import javax.sql.DataSource

class KommandokjedeDao(private val dataSource: DataSource): Dao(dataSource) {
    fun lagreSuspendert(kommandokjedeSuspendert: KommandokjedeSuspendertForDatabase) {
        TODO("Not yet implemented")
    }

    fun ferdigstilt(kommandokjedeFerdigstilt: KommandokjedeFerdigstiltForDatabase) {
        TODO("Not yet implemented")
    }


}