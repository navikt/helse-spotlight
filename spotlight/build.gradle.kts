private val rapidsAndRiversVersion = "2024022311041708682651.01821651ed22"
private val postgresqlVersion = "42.7.2"
private val hikariCPVersion = "5.1.0"
private val flywayCoreVersion = "10.7.1"
private val kotliqueryVersion = "1.9.0"
private val testcontainersPostgresqlVersion = "1.19.6"
private val junitJupiterVersion = "5.10.2"
private val mockkVersion = "1.13.9"
private val logbackVersion = "1.4.11"
private val logstashVersion = "7.4"

private val mainClass = "no.nav.helse.AppKt"

plugins {
    kotlin("jvm") version "1.9.22"
}

repositories {
    val githubPassword: String by project

    mavenCentral()
    maven {
        url = uri("https://maven.pkg.github.com/navikt/*")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation("com.github.navikt:rapids-and-rivers:$rapidsAndRiversVersion")

    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    implementation("org.flywaydb:flyway-core:$flywayCoreVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayCoreVersion")
    implementation("com.github.seratch:kotliquery:$kotliqueryVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion") {
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.dataformat")
    }

    testImplementation("org.testcontainers:postgresql:$testcontainersPostgresqlVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation(kotlin("test"))

}

tasks {
    kotlin {
        jvmToolchain(21)
    }
    test {
        useJUnitPlatform()
    }
    withType<Jar> {
        archiveBaseName.set("app")
        manifest {
            attributes["Main-Class"] = "no.nav.helse.AppKt"
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") {
                it.name
            }
        }
        doLast {
            configurations.runtimeClasspath.get().forEach {
                val file = File("${layout.buildDirectory.get()}/libs/${it.name}")
                if (!file.exists()) it.copyTo(file)
            }
        }
    }
}