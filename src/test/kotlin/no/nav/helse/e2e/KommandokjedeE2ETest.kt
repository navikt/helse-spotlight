package no.nav.helse.e2e

import no.nav.helse.Testdata.OPPRETTET
import org.junit.jupiter.api.Test

internal class KommandokjedeE2ETest: AbstractE2ETest() {

    @Test
    fun `Lagrer suspendert kommandokjede når kommandokjede_suspendert leses inn`() {
        sendKommandokjedeSuspendert()
        assertKommandokjedeLagret()
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
    fun `Poster på slack når klokka er 6`() {
        sendKommandokjedeSuspendert(opprettet = OPPRETTET.minusMinutes(35))
        sendHelTime()
        assertPostetDagligMeldingPåSlack()
    }

}