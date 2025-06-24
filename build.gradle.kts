plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("maven-publish")
}

// Default property values if gradle.properties is missing
val mod_version: String by project
val maven_group: String by project
val archives_base_name: String by project
val minecraft_version: String by project
val loader_version: String by project
val fabric_version: String by project

version = mod_version
group = maven_group

base {
    archivesName.set(archives_base_name)
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create("q-highlighter") {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

repositories {
    // Add your repositories here
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
    modImplementation(include("org.java-websocket:Java-WebSocket:1.6.0")!!)
}

tasks.processResources {
    inputs.property("version", version)
    inputs.property("minecraft_version", minecraft_version)
    inputs.property("loader_version", loader_version)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "minecraft_version" to minecraft_version,
            "loader_version" to loader_version
        )
    }
}

val targetJavaVersion = 21
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

java {
    withSourcesJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
}

tasks.jar {
    from("LICENSE.txt") {
        rename { "${it}_${base.archivesName.get()}" }
    }
}