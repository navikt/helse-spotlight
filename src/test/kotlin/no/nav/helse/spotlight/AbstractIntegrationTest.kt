package no.nav.helse.spotlight

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import kotliquery.sessionOf
import no.nav.helse.spotlight.db.KommandokjederDao
import no.nav.helse.spotlight.db.SqlRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime
import java.util.*

abstract class AbstractIntegrationTest {
    private val session = sessionOf(TestDatabase.dataSource, strict = true)
    protected val dao = KommandokjederDao(SqlRunner(session))

    protected val wireMockSlack: WireMockServer =
        WireMockServer(options().dynamicPort()).also(WireMockServer::start)

    protected val testRapid = TestRapid()

    protected val slackConfiguration = Configuration.Slack(
        accessToken = "mockAccessToken",
        channel = "mockChannel",
        url = wireMockSlack.baseUrl()
    )

    @BeforeEach
    fun setUp() {
        start(
            dataSource = TestDatabase.dataSource,
            rapidsConnection = testRapid,
            slackConfiguration = slackConfiguration
        )
    }

    @AfterEach
    fun tearDown() {
        dao.finnAlle()
            .map(Kommandokjede::commandContextId)
            .forEach(dao::slett)
        session.close()
        wireMockSlack.stop()
    }

    protected fun lagretKommandokjede(
        opprettet: LocalDateTime = LocalDateTime.now().minusMinutes(5),
        antallGangerPåminnet: Int = 0
    ): Kommandokjede =
        Kommandokjede(
            commandContextId = UUID.randomUUID(),
            meldingId = UUID.randomUUID(),
            command = "EnCommand",
            sti = listOf(0),
            opprettet = opprettet,
            antallGangerPåminnet = antallGangerPåminnet
        ).also(dao::lagre).also { checkNotNull(dao.finn(it.commandContextId)) }

    protected fun Kommandokjede.roundedToMicros() =
        copy(opprettet = opprettet.roundToMicros())

    protected fun LocalDateTime.roundToMicros(): LocalDateTime {
        val remainder = nano % 1000
        val withoutRemainder = 1000 * (nano / 1000)
        return withNano(withoutRemainder + if (remainder >= 500) 1000 else 0)
    }
}
