package no.nav.helse.spotlight

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage

object Meldingsbygger {
    fun byggKommandokjedePåminnelse(kommandokjede: Kommandokjede): String =
        JsonMessage.newMessage(
            "kommandokjede_påminnelse",
            mapOf(
                "commandContextId" to kommandokjede.commandContextId,
                "meldingId" to kommandokjede.meldingId
            )
        ).toJson()
}
