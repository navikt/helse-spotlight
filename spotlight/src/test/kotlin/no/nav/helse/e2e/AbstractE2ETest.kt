package no.nav.helse.e2e

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.TestRapidHelpers.hendelser
import no.nav.helse.Testmeldinger.halvTime
import no.nav.helse.Testmeldinger.helTime
import no.nav.helse.Testmeldinger.kommandokjedeAvbrutt
import no.nav.helse.Testmeldinger.kommandokjedeFerdigstilt
import no.nav.helse.Testmeldinger.kommandokjedeSuspendert
import no.nav.helse.db.DatabaseIntegrationTest
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.kafka.asUUID
import no.nav.helse.kafka.river.KommandokjedeFerdigstiltRiver
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.helse.slack.SlackClient
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals

internal abstract class AbstractE2ETest : DatabaseIntegrationTest() {

    protected val COMMAND_CONTEXT_ID: UUID = UUID.randomUUID()

    private val testRapid = TestRapid()
    private val slackClientMock = mockk<SlackClient>(relaxed = true)
    private val meldingssender = Meldingssender(testRapid, kommandokjedeDao)
    private val mediator = Mediator(testRapid, slackClientMock, meldingssender, kommandokjedeDao)

    init {
        KommandokjedeFerdigstiltRiver(testRapid, mediator)
    }

    @BeforeEach
    internal fun resetTestSetup() {
        testRapid.reset()
    }

    protected fun sendKommandokjedeSuspendert(
        commandContextId: UUID = COMMAND_CONTEXT_ID,
        opprettet: LocalDateTime = LocalDateTime.now()
    ) =
        testRapid.sendTestMessage(kommandokjedeSuspendert(commandContextId, opprettet))

    protected fun sendKommandokjedeFerdigstilt(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        testRapid.sendTestMessage(kommandokjedeFerdigstilt(commandContextId))

    protected fun sendKommandokjedeAvbrutt(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        testRapid.sendTestMessage(kommandokjedeAvbrutt(commandContextId))

    protected fun sendHalvTime() =
        testRapid.sendTestMessage(halvTime())

    protected fun sendHelTime(time: Int = 6) =
        testRapid.sendTestMessage(helTime(time))

    protected fun assertKommandokjedeLagret(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        assertLagret(commandContextId)

    protected fun assertKommandokjedeSlettet(commandContextId: UUID = COMMAND_CONTEXT_ID) =
        assertSlettet(commandContextId)

    protected fun assertPåminnelserSendt(commandContextId: UUID = COMMAND_CONTEXT_ID) {
        assertPåminnet(commandContextId, 1)
        assertEquals(
            commandContextId,
            testRapid.inspektør.hendelser("kommandokjede_påminnelse").first()["commandContextId"].asUUID()
        )
    }

    protected fun assertPostetPåSlack() {
        verify(exactly = 1) { mediator.fortellOmSuspenderteKommandokjeder() }
        verify(exactly = 1) { slackClientMock.fortellOmSuspenderteKommandokjeder(any()) }
    }

}