package no.nav.helse.e2e

import no.nav.helse.Testdata.OPPRETTET
import no.nav.helse.db.Tilstand.FEIL
import org.junit.jupiter.api.Test

internal class KommandokjedeE2ETest: AbstractE2ETest() {

    @Test
    fun `Lagrer suspendert kommandokjede når kommandokjede_suspendert leses inn`() {
        sendKommandokjedeSuspendert()
        assertKommandokjedeLagret()
    }

    @Test
    fun `Lagrer feilet kommandokjede når kommandokjede_feilet leses inn`() {
        sendKommandokjedeFeilet()
        assertKommandokjedeLagret()
    }

    @Test
    fun `Oppdaterer tilstand på kommandokjede når kommandokjede_feilet leses inn, hvis den allerede er suspendert`() {
        sendKommandokjedeSuspendert()
        sendKommandokjedeFeilet()
        assertKommandokjedeTilstand(FEIL)
    }

    @Test
    fun `Sletter suspendert kommandokjede når kommandokjede_ferdigstilt leses inn`() {
        sendKommandokjedeSuspendert()
        sendKommandokjedeFerdigstilt()
        assertKommandokjedeSlettet()
    }

    @Test
    fun `Sletter suspendert kommandokjede når kommandokjede_avbrutt leses inn`() {
        sendKommandokjedeSuspendert()
        sendKommandokjedeAvbrutt()
        assertKommandokjedeSlettet()
    }

    @Test
    fun `Sender påminnelse når halv_time leses inn`() {
        sendKommandokjedeSuspendert(opprettet = OPPRETTET.minusMinutes(35))
        sendHalvTime()
        assertPåminnelserSendt()
    }

    @Test
    fun `Poster på slack hvis det sendes påminnelse på kommandokjede med tilstand FEIL`() {
        sendKommandokjedeFeilet(opprettet = OPPRETTET.minusMinutes(35))
        sendHalvTime()
        assertPåminnelserSendt()
        assertPostetPåminnelseMeldingPåSlack()
    }

    @Test
    fun `Poster ikke på slack hvis det ikke sendes påminnelse på kommandokjede med tilstand FEIL`() {
        sendKommandokjedeSuspendert(opprettet = OPPRETTET.minusMinutes(35))
        sendHalvTime()
        assertPåminnelserSendt()
        assertPostetPåminnelseMeldingPåSlack(forventetAntall = 0)
    }

    @Test
    fun `Poster på slack når klokka er 6`() {
        sendKommandokjedeSuspendert(opprettet = OPPRETTET.minusMinutes(35))
        sendHelTime()
        assertPostetDagligMeldingPåSlack()
    }

}