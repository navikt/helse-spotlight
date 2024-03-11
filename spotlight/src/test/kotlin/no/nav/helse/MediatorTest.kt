package no.nav.helse

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.TestRapidHelpers.siste
import no.nav.helse.Testdata.kommandokjedeSuspendertForOverEnTimeSiden
import no.nav.helse.db.AbstractDatabaseTest
import no.nav.helse.kafka.asUUID
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.helse.slack.SlackClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class MediatorTest: AbstractDatabaseTest() {

    private val testRapid = TestRapid()
    private val slackClient = mockk<SlackClient>(relaxed = true)
    private val mediator = Mediator(testRapid, dataSource, slackClient)

    @BeforeEach
    internal fun resetTestSetup() {
        testRapid.reset()
    }

    @Test
    fun `sender melding om at ingen kommandokjeder sitter fast`() {
        mediator.fortellOmSuspenderteKommandokjeder()
        verify(exactly = 1) { slackClient.postMessage(text = ":spotlight: Ingen kommandokjeder sitter fast :spotlight:") }
    }

    @Test
    fun `sender én melding hvis det er under 50 stuck kommandokjeder`() {
        mediator.kommandokjedeSuspendert(kommandokjedeSuspendertForOverEnTimeSiden())
        mediator.fortellOmSuspenderteKommandokjeder()
        verify(exactly = 1) { slackClient.postMessage(attachments = any()) }
    }

    @Test
    fun `sender to meldinger hvis det er over 50 stuck kommandokjeder`() {
        every { slackClient.postMessage(attachments = any()) } returns "threadTs"
        repeat(51) { mediator.kommandokjedeSuspendert(kommandokjedeSuspendertForOverEnTimeSiden()) }
        mediator.fortellOmSuspenderteKommandokjeder()
        verify(exactly = 1) { slackClient.postMessage(attachments = any()) }
        verify(exactly = 1) { slackClient.postMessage(attachments = any(), threadTs = "threadTs") }
    }

    @Test
    fun `sender tre meldinger hvis det er over 100 stuck kommandokjeder`() {
        every { slackClient.postMessage(attachments = any()) } returns "threadTs"
        repeat(101) { mediator.kommandokjedeSuspendert(kommandokjedeSuspendertForOverEnTimeSiden()) }
        mediator.fortellOmSuspenderteKommandokjeder()
        verify(exactly = 1) { slackClient.postMessage(attachments = any()) }
        verify(exactly = 2) { slackClient.postMessage(attachments = any(), threadTs = "threadTs") }
    }

    @Test
    fun `sender kommandokjeder_påminnelse`() {
        val commandContextId1 = UUID.randomUUID()
        val commandContextId2 = UUID.randomUUID()
        val meldingId1 = UUID.randomUUID()
        val meldingId2 = UUID.randomUUID()
        mediator.kommandokjedeSuspendert(kommandokjedeSuspendertForOverEnTimeSiden(commandContextId1, meldingId1))
        mediator.kommandokjedeSuspendert(kommandokjedeSuspendertForOverEnTimeSiden(commandContextId2, meldingId2))
        mediator.påminnSuspenderteKommandokjeder()
        val melding = testRapid.inspektør.siste("kommandokjeder_påminnelse")
        assertEquals(commandContextId1, melding["kommandokjeder"][0]["commandContextId"].asUUID())
        assertEquals(commandContextId2, melding["kommandokjeder"][1]["commandContextId"].asUUID())
        assertEquals(meldingId1, melding["kommandokjeder"][0]["meldingId"].asUUID())
        assertEquals(meldingId2, melding["kommandokjeder"][1]["meldingId"].asUUID())
    }

}