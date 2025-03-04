// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.dynamic.feature) apply false
    id("jacoco")
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


