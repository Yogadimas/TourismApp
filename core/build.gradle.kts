plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("jacoco")
}

android {
    namespace = "com.yogadimas.tourismapp.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "BASE_URL_API", "\"${properties["BASE_URL_API"]}\"")
        buildConfigField("String", "BASE_URL_HOST", "\"${properties["BASE_URL_HOST"]}\"")
        buildConfigField("String", "CERT_SHA_256_1", "\"${properties["CERT_SHA_256_1"]}\"")
        buildConfigField("String", "CERT_SHA_256_2", "\"${properties["CERT_SHA_256_2"]}\"")
        buildConfigField("String", "CERT_SHA_256_3", "\"${properties["CERT_SHA_256_3"]}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        buildConfig = true
    }
    testCoverage { jacocoVersion = "0.8.12" }
}

dependencies {

    implementation(libs.android.database.sqlcipher)
    implementation(libs.androidx.sqlite.ktx)

    debugImplementation(libs.leakcanary.android)

    api(libs.recyclerview)

    api(libs.koin.android)

    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.glide)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.activity.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn(
        "testDebugUnitTest",
        "processDebugManifest",
        "compileDebugLibraryResources",
        "compileReleaseJavaWithJavac",
        "dataBindingGenBaseClassesRelease",
        "kspReleaseKotlin",
        "packageReleaseResources",
        "generateReleaseResValues",
        "processReleaseManifest",
        "syncDebugLibJars",
        "compileReleaseLibraryResources",
        "mergeDebugJavaResource",
        "mergeDebugJniLibFolders",
        "mergeDebugShaders",
        "mergeReleaseShaders",
        "packageDebugAssets",
        "packageReleaseAssets",
        "processReleaseJavaRes",
        "copyDebugJniLibsProjectAndLocalJars",
        "exportDebugConsumerProguardFiles",
        "exportReleaseConsumerProguardFiles",
        "copyDebugJniLibsProjectOnly",
        "generateDebugAndroidTestResValues",
        "syncReleaseLibJars",
        "lintAnalyzeDebugAndroidTest",
        "mergeReleaseResources",
        "extractProguardFiles",
        "mergeReleaseJniLibFolders",
        "copyReleaseJniLibsProjectAndLocalJars",
        "generateDebugLintModel",
        "lintAnalyzeDebug",
        "lintVitalAnalyzeRelease",
        "generateDebugAndroidTestLintModel",
        "copyReleaseJniLibsProjectOnly",
        "generateReleaseLintModel",
        "generateDebugLintReportModel",
        "generateReleaseLintVitalModel",
        "generateDebugUnitTestLintModel",
        "lintAnalyzeDebugUnitTest",
        "verifyReleaseResources",
        "ktlintAndroidTestSourceSetCheck",
        "ktlintKotlinScriptCheck",
        "ktlintMainSourceSetCheck",
        "ktlintTestSourceSetCheck",
    )

    executionData.setFrom(fileTree(layout.buildDirectory) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
    })

    classDirectories.setFrom(
        fileTree(layout.buildDirectory) {
            include("**/classes/**/main/**",
                "**/intermediates/classes/debug/**",
                "**/tmp/kotlin-classes/debug/**")
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