plugins {
    kotlin("jvm")
}

group = "cn.afeibaili"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Common"))
    testImplementation(kotlin("test"))
}

tasks.register<Jar>("dist") {
    dependsOn("jar")
    group = "build"

    from(tasks.jar.get().outputs.files.map { if (it.isDirectory) it else zipTree(it) })
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes("Main-Class" to "MainKt")
    }

    archiveFileName = "${rootProject.name}-SAC-${rootProject.extra.get("version")}.jar"
    destinationDirectory = File("/build/jar")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}