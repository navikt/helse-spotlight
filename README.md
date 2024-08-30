# Spotlight
[![Bygg og deploy Spinnvill](https://github.com/navikt/helse-spotlight/actions/workflows/main.yml/badge.svg)](https://github.com/navikt/helse-spotlight/actions/workflows/main.yml)

## Beskrivelse
Backend som lagrer ned kommandokjeder som sitter fast i [Spesialist](https://github.com/navikt/helse-spotlight) og påminner dem.
Varsler på Slack hver morgen om kommandokjeder som er stuck.

## Format på Commit-meldinger 
I dette repoet skal det commites på dette formatet:
- `[gitmoji] [teksten din]`

  gitmoji finner du her: (https://gitmoji.dev/)
- Eksempel: ✅ Test automatisering uten varsel

NB: Husk å bytte til å bruke unicode characters i stedet for `:<emote>:` notasjon

## Manuell påminnelse
Fyll ut denne og send den fra spout.ansatt.dev.nav.no eller spout.intern.nav.noª:
```json
{
  "@event_name": "kommandokjede_påminnelse",
  "@id": "{{uuidgen}}",
  "commandContextId": "<fyll meg ut>",
  "meldingId": "<fyll meg ut>"
}
```
Dataene kan du finne i logger eller databasen til spesialist (eller spotlight).ª

## Henvendelser
Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

### For NAV-ansatte
Interne henvendelser kan sendes via Slack i kanalen [#team-bømlo-værsågod](https://nav-it.slack.com/archives/C019637N90X).
