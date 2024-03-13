package no.nav.helse.kafka

import io.mockk.mockk
import no.nav.helse.TestRapidHelpers.hendelser
import no.nav.helse.Testdata.kommandokjedeSuspendertFraDatabase
import no.nav.helse.db.KommandokjedeDao
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class MeldingssenderTest {
    private val testRapid = TestRapid()
    private val kommandokjedeDaoMock = mockk<KommandokjedeDao>(relaxed = true)
    private val meldingssender = Meldingssender(testRapid, kommandokjedeDaoMock)

    @BeforeEach
    internal fun resetTestSetup() {
        testRapid.reset()
    }

    @Test
    fun `sender kommandokjede_påminnelse`() {
        val commandContextId1 = UUID.randomUUID()
        val commandContextId2 = UUID.randomUUID()
        val meldingId1 = UUID.randomUUID()
        val meldingId2 = UUID.randomUUID()

        meldingssender.påminnSuspenderteKommandokjeder(
            listOf(
                kommandokjedeSuspendertFraDatabase(commandContextId1, meldingId1),
                kommandokjedeSuspendertFraDatabase(commandContextId2, meldingId2)
            )
        )

        val hendelser = testRapid.inspektør.hendelser("kommandokjede_påminnelse")
        assertEquals(commandContextId1, hendelser[0]["commandContextId"].asUUID())
        assertEquals(commandContextId2, hendelser[1]["commandContextId"].asUUID())
        assertEquals(meldingId1, hendelser[0]["meldingId"].asUUID())
        assertEquals(meldingId2, hendelser[1]["meldingId"].asUUID())
    }
}