plugins {
    alias(libs.plugins.android.dynamic.feature)
    alias(libs.plugins.kotlin.android)
    id("jacoco")
}
android {
    namespace = "com.yogadimas.tourismapp.favorite"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
    testCoverage { jacocoVersion = "0.8.12" }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":app"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(
        "testDebugUnitTest",
        "generateReleaseResValues",
        "copyReleaseMergedManifest",
        "featureReleaseWriter",
        "mergeDebugJavaResource",
        "compileReleaseJavaWithJavac",
        "dataBindingGenBaseClassesRelease",
        "mergeReleaseResources",
        "checkReleaseAarMetadata",
        "dataBindingMergeDependencyArtifactsRelease",
        "mergeReleaseJavaResource",
        "processReleaseJavaRes",
        "generateDebugAndroidTestResValues",
        "generateDebugAndroidTestLintModel",
        "lintAnalyzeDebugAndroidTest",
        "mergeDebugAssets",
        "mergeReleaseAssets",
        "compressDebugAssets",
        "compressReleaseAssets",
        "extractProguardFiles",
        "dexBuilderDebug",
        "dexBuilderRelease",
        "mergeExtDexDebug",
        "mergeLibDexDebug",
        "mergeProjectDexDebug",
        "mergeDexRelease",
        "checkDebugDuplicateClasses",
        "mergeReleaseGlobalSynthetics",
        "mergeDebugJniLibFolders",
        "mergeReleaseJniLibFolders",
        "packageDebug",
        "generateDebugLintModel",
        "generateReleaseLintModel",
        "lintAnalyzeDebug",
        "lintVitalAnalyzeRelease",
        "mergeReleaseNativeLibs",
        "extractReleaseNativeSymbolTables",
        "optimizeReleaseResources",
        "stripReleaseDebugSymbols",
        "generateDebugUnitTestLintModel",
        "lintAnalyzeDebugUnitTest"
    )

    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })

    classDirectories.setFrom(
        fileTree(layout.buildDirectory) {
            include(
                "**/classes/**/main/**",
                "**/intermediates/classes/debug/**",
                "**/tmp/kotlin-classes/debug/**"
            )
            exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.class", "**/Manifest*.*")
        }
    )

    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<Test> {
    finalizedBy(tasks.named("jacocoTestReport"))
}