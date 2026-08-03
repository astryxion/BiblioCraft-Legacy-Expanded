import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    idea
    id("net.fabricmc.fabric-loom")
    `maven-publish`
}

version = property("mod.version") as String
group = property("mod.group") as String

base {
    archivesName.set(property("mod.archives.name") as String)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of((property("java_version") as String).toInt()))
    }
    withSourcesJar()
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://maven.neoforged.net/releases") }
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

val abnormalsCompat = false

val apiSourceSet = sourceSets.create("api") {
    java.srcDir("src/api/java")
}
sourceSets.named("main") {
    compileClasspath += apiSourceSet.output
    runtimeClasspath += apiSourceSet.output
    resources.srcDir("src/main/generated")
}
val mainSourceSet = sourceSets.named("main")
sourceSets.create("data") {
    java.srcDir("src/data/java")
    compileClasspath += mainSourceSet.get().output + apiSourceSet.output
    runtimeClasspath += mainSourceSet.get().output + apiSourceSet.output
}
sourceSets.named("test") {
    compileClasspath += mainSourceSet.get().output + apiSourceSet.output
    runtimeClasspath += mainSourceSet.get().output + apiSourceSet.output
}

loom {
    accessWidenerPath = file("src/main/resources/bibliocraft.classtweaker")

    mods {
        create("bibliocraft") {
            sourceSet(apiSourceSet)
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("data"))
        }
    }

    runs {
        configureEach {
            ideConfigGenerated(true)
        }
        named("client") {
            client()
        }
        named("server") {
            server()
        }
        create("datagen") {
            client()
            name = "Data Generation"
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources").absolutePath}")
            vmArg("-Dfabric-api.datagen.modid=${property("mod.id")}")
            runDir("build/datagen")
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${property("mc_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    compileOnly("net.neoforged:neoforge:${property("neo_version")}:universal")
    runtimeOnly("net.neoforged:neoforge:${property("neo_version")}:universal")
    compileOnly("net.neoforged.fancymodloader:loader:11.0.12")
    compileOnly("net.neoforged:bus:8.0.5")
    runtimeOnly("net.neoforged.fancymodloader:loader:11.0.12")
    runtimeOnly("net.neoforged:bus:8.0.5")
    add("apiCompileOnly", "net.neoforged:neoforge:${property("neo_version")}:universal")
    add("apiCompileOnly", "net.neoforged.fancymodloader:loader:11.0.12")
    add("apiCompileOnly", "net.neoforged:bus:8.0.5")

    configurations.named("apiCompileClasspath") {
        extendsFrom(configurations.getByName("compileClasspath"))
    }
    configurations.named("apiRuntimeClasspath") {
        extendsFrom(configurations.getByName("runtimeClasspath"))
    }
    configurations.named("dataCompileClasspath") {
        extendsFrom(configurations.getByName("compileClasspath"))
    }
    configurations.named("dataRuntimeClasspath") {
        extendsFrom(configurations.getByName("runtimeClasspath"))
    }
    configurations.named("testCompileClasspath") {
        extendsFrom(configurations.getByName("compileClasspath"))
    }
    configurations.named("testRuntimeClasspath") {
        extendsFrom(configurations.getByName("runtimeClasspath"))
    }

    // jei for integration
    val jeiVersion = property("dependency.jei.version") as String
    val mcVersion = property("mc_version") as String
    compileOnly("mezz.jei:jei-${mcVersion}-fabric-api:${jeiVersion}")
    if (System.getenv("CI") == null) {
        runtimeOnly("mezz.jei:jei-${mcVersion}-fabric:${jeiVersion}")
    }

    // abnormals mods for integration
    //if (abnormalsCompat) {
        //runtimeOnly("curse.maven:blueprint-382216:6449863")
        //runtimeOnly("curse.maven:buzzier-bees-355458:6449894")
    //}

    testImplementation("org.junit.jupiter:junit-jupiter:${property("junit_version")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    listOf("compileOnly", "apiCompileOnly", "dataCompileOnly", "testCompileOnly").forEach { config ->
        add(config, "org.jetbrains:annotations:23.0.0")
    }
}

tasks.processResources {
    val modVersion = version
    inputs.property("version", modVersion)

    filesMatching("fabric.mod.json") {
        expand("version" to modVersion)
    }
}

tasks.register<Jar>("apiJar") {
    archiveClassifier.set("api")
    from(apiSourceSet.output)
}

tasks.named<Jar>("jar") {
    from(apiSourceSet.output)
    val licenseArchiveName = project.property("mod.archives.name") as String
    from("LICENSE") {
        rename { "${it}_$licenseArchiveName" }
    }
}

tasks.javadoc {
    classpath = apiSourceSet.compileClasspath
    source = apiSourceSet.allJava
}

tasks.test {
    // TODO how do I make the tests work correctly with this
    enabled = false
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(arrayOf("-Xlint:-removal", "-Xmaxerrs", "9999"))
}

publishing {
    publications {
        create<MavenPublication>("bibliocraftToMaven") {
            from(components["java"])
            pom {
                name.set(property("mod.name") as String)
                description.set(property("mod.description") as String)
                url.set(property("mod.url") as String)
                licenses {
                    license {
                        name.set(property("license.name") as String)
                        url.set(property("license.url") as String)
                    }
                }
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
                    }
                    developer {
                        id.set("ichhabehunger54")
                        name.set("IchHabeHunger54")
                        url.set("https://github.com/IchHabeHunger54")
                        organization.set("Minecraftschurli Mods")
                        organizationUrl.set("https://github.com/MinecraftschurliMods")
                    }
                }
            }
        }
    }
}
