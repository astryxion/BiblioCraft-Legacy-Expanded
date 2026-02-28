plugins {
    idea
    id("fabric-loom") version "1.6-SNAPSHOT"
    id("maven-publish")
}

version = project.properties["mod.version"] as String
group = project.properties["maven_group"] as String

base {
    archivesName.set(project.properties["archives_base_name"] as String)
}

// Api source set: main compiles against it; api needs Minecraft/deps to compile
val api by sourceSets.creating {
    compileClasspath += configurations["compileClasspath"]
    runtimeClasspath += configurations["runtimeClasspath"]
}
// Main compiles against api; exclude JEI package (compileOnly JEI uses intermediary mappings, incompatible with Mojang)
sourceSets.main.get().apply {
    compileClasspath += api.output
    runtimeClasspath += api.output
    java.exclude("**/client/jei/**")
    // Include generated assets (blockstates, item models) so they are packaged into the mod JAR
    resources.srcDir("src/main/generated")
}
// Data source set: datagen (depends on main + api)
val data by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + api.output + configurations["compileClasspath"]
    runtimeClasspath += sourceSets.main.get().output + api.output + configurations["runtimeClasspath"]
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "blamejared Maven"
        url = uri("https://maven.blamejared.com")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }
}

val minecraft_version: String by project
val loader_version: String by project
val fabric_version: String by project
val junit_version: String by project

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("net.fabricmc.fabric-api:fabric-api:$fabric_version")

    compileOnly("org.jetbrains:annotations:23.0.0")

    // JEI (optional integration – Fabric)
    compileOnly("mezz.jei:jei-$minecraft_version-fabric-api:19.21.0.243")

    testImplementation("org.junit.jupiter:junit-jupiter:$junit_version")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
// Annotations for api, data, test source sets
sourceSets.matching { it.name != "main" }.forEach { ss ->
    configurations[ss.compileOnlyConfigurationName].dependencies.add(
        project.dependencies.compileOnly("org.jetbrains:annotations:23.0.0")!!
    )
}

loom {
    mods {
        create("bibliocraft") {
            sourceSet(sourceSets.main.get())
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.javadoc {
    source = api.allJava
    classpath = api.compileClasspath
}

tasks.jar {
    from(sourceSets["api"].output)
    from("LICENSE") {
        rename { "_${project.base.archivesName.get()}_$it" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.properties["archives_base_name"] as String
            from(components["java"])
            pom {
                name.set(project.properties["mod.name"] as String)
                description.set(project.properties["mod.description"] as String)
                url.set(project.properties["mod.url"] as String)
                organization {
                    name.set("Minecraftschurli Mods")
                    url.set("https://github.com/MinecraftschurliMods")
                }
                developers {
                    developer {
                        id.set("minecraftschurli")
                        name.set("Minecraftschurli")
                        email.set("minecraftschurli@gmail.com")
                        url.set("https://github.com/Minecraftschurli")
                        organization.set("Minecraftschurli Mods")
                        organizationUrl.set("https://github.com/MinecraftschurliMods")
                        timezone.set("Europe/Vienna")
                    }
                    developer {
                        id.set("ichhabehunger54")
                        name.set("IchHabeHunger54")
                        url.set("https://github.com/IchHabeHunger54")
                        organization.set("Minecraftschurli Mods")
                        organizationUrl.set("https://github.com/MinecraftschurliMods")
                        timezone.set("Europe/Vienna")
                    }
                }
            }
        }
    }
}
