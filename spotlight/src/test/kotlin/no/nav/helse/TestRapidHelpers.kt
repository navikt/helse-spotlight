package no.nav.helse

import no.nav.helse.rapids_rivers.testsupport.TestRapid

internal object TestRapidHelpers {
    internal fun TestRapid.RapidInspector.meldinger() =
        (0 until size).map { index -> message(index) }

    internal fun TestRapid.RapidInspector.hendelser(type: String) =
        meldinger().filter { it.path("@event_name").asText() == type }

}
