package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.Testdata.kommandokjedeAvbruttTilDatabase
import no.nav.helse.Testmeldinger.kommandokjedeAvbrutt
import no.nav.helse.kafka.river.KommandokjedeAvbruttRiver
import org.junit.jupiter.api.Test

internal class KommandokjedeAvbruttRiverTest {

    private val testRapid = TestRapid()
    private val mediatorMock = mockk<Mediator>(relaxed = true)

    init {
        KommandokjedeAvbruttRiver(testRapid, mediatorMock)
    }

    @Test
    fun `Kan lese inn kommandokjede_avbrutt`() {
        testRapid.sendTestMessage(kommandokjedeAvbrutt())
        verify(exactly = 1) { mediatorMock.kommandokjedeAvbrutt(kommandokjedeAvbruttTilDatabase()) }
    }

}
