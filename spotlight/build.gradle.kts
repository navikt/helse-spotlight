private val rapidsAndRiversVersion = "2024022311041708682651.01821651ed22"
plugins {
    kotlin("jvm") version "1.9.22"
}


repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("com.github.navikt:rapids-and-rivers:$rapidsAndRiversVersion")
    testImplementation(kotlin("test"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}