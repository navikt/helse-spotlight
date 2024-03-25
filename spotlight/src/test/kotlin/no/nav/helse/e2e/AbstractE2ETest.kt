package no.nav.helse.e2e

import io.mockk.mockk
import io.mockk.verify
import no.nav.helse.Mediator
import no.nav.helse.TestRapidHelpers.hendelser
import no.nav.helse.Testdata.COMMAND_CONTEXT_ID
import no.nav.helse.Testdata.OPPRETTET
import no.nav.helse.Testmeldinger.halvTime
import no.nav.helse.Testmeldinger.helTime
import no.nav.helse.Testmeldinger.kommandokjedeAvbrutt
import no.nav.helse.Testmeldinger.kommandokjedeFeilet
import no.nav.helse.Testmeldinger.kommandokjedeFerdigstilt
import no.nav.helse.Testmeldinger.kommandokjedeSuspendert
import no.nav.helse.db.DatabaseIntegrationTest
import no.nav.helse.db.Tilstand
import no.nav.helse.kafka.Meldingssender
import no.nav.helse.kafka.asUUID
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.helse.slack.SlackClient
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import kotlin.test.assertEquals

internal abstract class AbstractE2ETest : DatabaseIntegrationTest() {

    private val testRapid = TestRapid()
    private val slackClientMock = mockk<SlackClient>(relaxed = true)
    private val meldingssender = Meldingssender(testRapid)
    private val mediator = Mediator(testRapid, slackClientMock, meldingssender, kommandokjedeDao)

    @BeforeEach
    internal fun resetTestSetup() {
        testRapid.reset()
    }

    protected fun sendKommandokjedeSuspendert(opprettet: LocalDateTime = OPPRETTET) =
        testRapid.sendTestMessage(kommandokjedeSuspendert(opprettet))

    protected fun sendKommandokjedeFeilet(opprettet: LocalDateTime = OPPRETTET) =
        testRapid.sendTestMessage(kommandokjedeFeilet(opprettet))

    protected fun sendKommandokjedeFerdigstilt() = testRapid.sendTestMessage(kommandokjedeFerdigstilt())

    protected fun sendKommandokjedeAvbrutt() = testRapid.sendTestMessage(kommandokjedeAvbrutt())

    protected fun sendHalvTime() = testRapid.sendTestMessage(halvTime())

    protected fun sendHelTime() = testRapid.sendTestMessage(helTime())

    protected fun assertKommandokjedeLagret() = assertLagret()

    protected fun assertKommandokjedeTilstand(tilstand: Tilstand) = assertTilstand(tilstand = tilstand)

    protected fun assertKommandokjedeSlettet() = assertSlettet()

    protected fun assertPåminnelserSendt() {
        assertPåminnet(forventetAntallGangerPåminnet = 1)
        assertEquals(
            COMMAND_CONTEXT_ID,
            testRapid.inspektør.hendelser("kommandokjede_påminnelse").first()["commandContextId"].asUUID()
        )
    }

    protected fun assertPostetDagligMeldingPåSlack() {
        verify(exactly = 1) { mediator.fortellOmKommandokjeder() }
        verify(exactly = 1) { slackClientMock.fortellOmKommandokjeder(any()) }
    }

    protected fun assertPostetPåminnelseMeldingPåSlack(forventetAntall: Int = 1) {
        verify(exactly = forventetAntall) { slackClientMock.fortellOmKommandokjederPåminnetMedTilstandFeil(any()) }
    }

}