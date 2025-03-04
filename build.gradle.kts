import org.gradle.kotlin.dsl.libs
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

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
        outputDirectory = layout.buildDirectory.dir("reports/dependency-check").get().asFile.absolutePath
        scanConfigurations = listOf("runtimeClasspath", "compileClasspath")
        analyzers {
            assemblyEnabled = false
            jarEnabled = true
        }
        nvd {
            apiKey = "5aa5b2fc-32d3-4271-aa08-065bea997406"
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


