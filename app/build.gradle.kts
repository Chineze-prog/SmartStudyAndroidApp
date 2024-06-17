import com.diffplug.gradle.spotless.SpotlessTask

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.android.code.quality)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    //id("com.google.devtools.ksp")
    alias(libs.plugins.spotless)
    id("androidx.room")
}

android {
    namespace = "com.example.studysmartandroidapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.studysmartandroidapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

hilt { enableAggregatingTask = true }

codeQuality {
    reportsDirectory.set(layout.buildDirectory.dir("reports"))
    issuesFile.set(reportsDirectory.file("code-quality-issues.json"))
}

spotless {
    format("misc") {
        target(".gitattributes", ".gitignore")

        trimTrailingWhitespace()
        indentWithSpaces(2)
        endWithNewline()
    }
    kotlin {
        target("**/*.kt")
        ktfmt("0.47").kotlinlangStyle()
    }
    kotlinGradle { ktfmt("0.47").kotlinlangStyle() }
}

tasks.named("collectCodeQualityIssues") { dependsOn("lint") }

tasks.named("lint") { dependsOn("spotlessCheck") }

tasks.withType<SpotlessTask>().configureEach {
    notCompatibleWithConfigurationCache("https://github.com/diffplug/spotless/issues/987")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // compose navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.compose.navigation)

    // compose Room
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin Symbol Processing (KSP)
    kapt(libs.androidx.room.compiler)
    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.common)

    //Dagger-Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    //google devtools
    implementation(libs.com.google.devtools.ksp.gradle.plugin)

    //Desurgaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    implementation(libs.androidx.ui.text.google.fonts)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}