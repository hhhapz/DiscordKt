import org.jetbrains.dokka.Platform

group = "me.jakejmattson"
version = "0.21.3"
val isSnapshot = version.toString().endsWith("SNAPSHOT")

plugins {
    //Core
    kotlin("jvm") version Versions.kotlin
    kotlin("plugin.serialization") version Versions.kotlin
    id("org.jetbrains.dokka") version Versions.dokka

    //Publishing
    signing
    `maven-publish`
    id("io.codearte.nexus-staging") version "0.22.0"

    //Misc
    id("com.github.ben-manes.versions") version "0.33.0"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    //Internal Dependencies
    implementation(Dependencies.coroutines)
    implementation(Dependencies.reflections)
    implementation(Dependencies.slf4j)
    implementation(Dependencies.gson)

    //Library Dependencies
    api(Dependencies.kord)
    api(Dependencies.emojis)
}

tasks {
    val resourcePath = "src/main/resources"

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    copy {
        val path = "$resourcePath/templates/readme.md"

        from(file(path))
        into(file("."))
        rename { "README.md" }
        expand(
            "badges" to README.badges,
            "imports" to README.createImport(group.toString(), version.toString(), isSnapshot)
        )
    }

    copy {
        from(file("$resourcePath/templates/properties-template.json"))
        into(file(resourcePath))
        rename { "library-properties.json" }
        expand(
            "projectRepo" to Constants.projectUrl,
            "projectVersion" to version,
            "kotlinVersion" to Versions.kotlin,
            "kordVersion" to Versions.kord
        )
    }

    dokkaHtml.configure {
        outputDirectory.set(buildDir.resolve("javadoc"))

        dokkaSourceSets {
            configureEach {
                platform.set(Platform.jvm)
                jdkVersion.set(8)

                includeNonPublic.set(false)
                skipEmptyPackages.set(true)
                reportUndocumented.set(true)

                suppressedFiles.from("src\\main\\kotlin\\me\\jakejmattson\\discordkt\\api\\GenericContainers.kt")
            }
        }
    }

    register("dependencySizes") {
        description = "Print dependency sizes for the default configuration"
        doLast {
            val sizes = buildString {
                val configuration = configurations["default"]
                val size = configuration.map { it.length() / (1024.0 * 1024.0) }.sum()
                val longestName = configuration.map { it.name.length }.max()
                val formatStr = "%-${longestName}s   %5d KB"

                appendln("Total Size: %.2f MB\n".format(size))

                configuration
                    .sortedBy { -it.length() }
                    .forEach {
                        appendln(formatStr.format(it.name, it.length() / 1024))
                    }
            }

            println(sizes)
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
    dependsOn(tasks.dokkaHtml)
}

publishing {
    publications {
        create<MavenPublication>(Constants.projectName) {
            from(components["kotlin"])
            artifact(sourcesJar)
            artifact(dokkaJar)

            pom {
                name.set(Constants.projectName)
                description.set(Constants.projectDescription)
                url.set(Constants.projectUrl)
                developers {
                    developer {
                        id.set("JakeJMattson")
                        name.set("Jake Mattson")
                        email.set("JakeJMattson@gmail.com")
                    }
                }
                withXml {
                    val repoNode = asNode().appendNode("repositories").appendNode("repository")

                    with(repoNode) {
                        appendNode("id", "jcenter")
                        appendNode("name", "jcenter-bintray")
                        appendNode("url", "https://jcenter.bintray.com")
                    }
                }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                scm {
                    connection.set("scm:git:ssh://github.com/JakeJMattson/DiscordKt.git")
                    developerConnection.set("scm:git:ssh://git@github.com:JakeJMattson/DiscordKt.git")
                    url.set(Constants.projectUrl)
                }
            }
            repositories {
                maven {
                    url = if (isSnapshot) uri(Constants.snapshotsRepoUrl) else uri(Constants.releasesRepoUrl)

                    credentials {
                        username = project.properties["nexusUsername"] as String?
                        password = project.properties["nexusPassword"] as String?
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications[Constants.projectName])
}

nexusStaging { }