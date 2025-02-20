package no.nav.helse

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid

internal object TestRapidHelpers {

    internal fun TestRapid.RapidInspector.meldinger() =
        (0 until size).map { index -> message(index) }

    internal fun TestRapid.RapidInspector.hendelser(type: String) =
        meldinger().filter { it.path("@event_name").asText() == type }

}
