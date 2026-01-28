pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
        kotlin("jvm") version "2.2.20"
    }
}

rootProject.name = "TCPForwarding"
include("Client")
include("Server")
include("Common")