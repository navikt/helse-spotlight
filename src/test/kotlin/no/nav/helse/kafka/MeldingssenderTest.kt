package no.nav.helse.kafka

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import no.nav.helse.TestRapidHelpers.hendelser
import no.nav.helse.Testdata.kommandokjedeFraDatabase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

internal class MeldingssenderTest {

    private val testRapid = TestRapid()
    private val meldingssender = Meldingssender(testRapid)

    @BeforeEach
    internal fun resetTestSetup() {
        testRapid.reset()
    }

    @Test
    fun `Sender kommandokjede_påminnelse`() {
        val commandContextId1 = UUID.randomUUID()
        val commandContextId2 = UUID.randomUUID()
        val meldingId1 = UUID.randomUUID()
        val meldingId2 = UUID.randomUUID()

        meldingssender.påminnKommandokjeder(
            listOf(
                kommandokjedeFraDatabase(commandContextId1, meldingId1),
                kommandokjedeFraDatabase(commandContextId2, meldingId2)
            )
        )

        val hendelser = testRapid.inspektør.hendelser("kommandokjede_påminnelse")
        assertEquals(commandContextId1, hendelser[0]["commandContextId"].asUUID())
        assertEquals(commandContextId2, hendelser[1]["commandContextId"].asUUID())
        assertEquals(meldingId1, hendelser[0]["meldingId"].asUUID())
        assertEquals(meldingId2, hendelser[1]["meldingId"].asUUID())
    }

}
