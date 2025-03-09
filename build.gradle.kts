
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    id("jacoco")
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.owaspDependencyCheck) apply false
}


jacoco {
    toolVersion = "0.8.12"
}

tasks.register<JacocoReport>("jacocoFullReport") {
    dependsOn(
        subprojects.flatMap { subproject ->
            subproject.tasks.matching {
                it.name == "testDebugUnitTest" || it.name == "jacocoTestReport"
            }.toList()
        }
    )

    executionData.setFrom(
        fileTree(rootProject.rootDir) {
            include("**/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }.filter { it.exists() }
    )

    val coverageSourceDirs = subprojects.flatMap { subproject ->
        listOf(
            "${subproject.projectDir}/src/main/java",
            "${subproject.projectDir}/src/main/kotlin"
        )
    }.filter { File(it).exists() }

    val classFiles = subprojects.map { subproject ->
        fileTree(subproject.layout.buildDirectory.get().asFile).apply {
            include(
                "**/classes/**/main/**",
                "**/intermediates/classes/debug/**",
                "**/tmp/kotlin-classes/debug/**"
            )
            exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.class", "**/Manifest*.*")
        }
    }.filter { it.files.isNotEmpty() }

    sourceDirectories.setFrom(files(coverageSourceDirs))
    classDirectories.setFrom(files(classFiles))

    reports {
        xml.required.set(true)
        html.required.set(true)

        xml.outputLocation.set(rootProject.layout.buildDirectory.file("jacoco/report.xml"))
        html.outputLocation.set(rootProject.layout.buildDirectory.dir("jacoco/html"))

        doLast {
            println("✅ Merged JaCoCo report generated at: ${rootProject.layout.buildDirectory.get().asFile}/jacoco/html/index.html")
        }
    }
}

tasks.withType<Test> {
    finalizedBy(tasks.named("jacocoFullReport"))
}

private val localProperties = Properties().apply {
    load(File(rootDir, "local.properties").inputStream())
}

private fun getEnvOrProperty(key: String): String {
    return System.getenv(key) ?: localProperties.getProperty(key, "")
}

private val apiKeyNvd = getEnvOrProperty("API_KEY_NVD")
private val baseUrlApi = getEnvOrProperty("BASE_URL_API")
private val baseUrlHost = getEnvOrProperty("BASE_URL_HOST")
private val certSha2561 = getEnvOrProperty("CERT_SHA_256_1")
private val certSha2562 = getEnvOrProperty("CERT_SHA_256_2")
private val certSha2563 = getEnvOrProperty("CERT_SHA_256_3")

extensions.extraProperties["BASE_URL_API"] = baseUrlApi
extensions.extraProperties["BASE_URL_HOST"] = baseUrlHost
extensions.extraProperties["CERT_SHA_256_1"] = certSha2561
extensions.extraProperties["CERT_SHA_256_2"] = certSha2562
extensions.extraProperties["CERT_SHA_256_3"] = certSha2563

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.owasp.dependencycheck")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        debug.set(true)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(true)
        enableExperimentalRules.set(true)
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.HTML)
        }
        kotlinScriptAdditionalPaths {
            include(fileTree("scripts/"))
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = "1.23.8"
        buildUponDefaultConfig = true
        autoCorrect = false
        ignoreFailures = true
    }


    configure<org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension> {
        failBuildOnCVSS = 7.0f
        format = "HTML"
        outputDirectory =
            layout.buildDirectory.dir("reports/dependency-check").get().asFile.absolutePath
        scanConfigurations = listOf("runtimeClasspath", "compileClasspath")
        analyzers {
            assemblyEnabled = false
            jarEnabled = true
        }
        nvd {
            apiKey = apiKeyNvd
        }
    }


}

tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.GenerateReportsTask> {
    reportsOutputDirectory.set(
        project.layout.buildDirectory.dir("ktlint/$name")
    )
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    reports {
        html {
            required.set(true)
            outputLocation.set(file("${project.layout.buildDirectory}/reports/detekt/report.html"))
        }
        xml {
            required.set(true)
            outputLocation.set(file("${project.layout.buildDirectory}/reports/detekt/report.xml"))
        }
        txt.required.set(false)
        sarif.required.set(false)
    }
}


