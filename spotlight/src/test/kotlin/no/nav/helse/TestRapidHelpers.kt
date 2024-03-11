package no.nav.helse

import no.nav.helse.rapids_rivers.testsupport.TestRapid

object TestRapidHelpers {
    fun TestRapid.RapidInspector.meldinger() =
        (0 until size).map { index -> message(index) }

    fun TestRapid.RapidInspector.hendelser(type: String) =
        meldinger().filter { it.path("@event_name").asText() == type }

    fun TestRapid.RapidInspector.siste(type: String) =
        hendelser(type).last()

}
