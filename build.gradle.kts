plugins {
    alias(libs.plugins.sas.deployable)
}

sasDeployable {
    mainClass = "no.nav.helse.spotlight.AppKt"
}

dependencies {
    implementation(libs.rapidsAndRivers)

    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    implementation(libs.flyway.core)
    implementation(libs.flyway.databasePostgresql)
    implementation(libs.kotliquery)
    implementation(libs.micrometer.registryPrometheus)

    implementation(libs.logback.classic)
    implementation(libs.logback.logstashEncoder) {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.dataformat")
    }

    testImplementation(libs.wiremock)
    testImplementation(libs.tbdLibs.rapidsAndRiversTest)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.mockk)
}
