
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
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
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    //implementation(libs.androidx.navigation.safe.args.generator)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //destination compose - navigation
    //val destinationVersion = "2.7.7"
    implementation(libs.androidx.navigation.compose)
    //implementation (libs.core)
    //noinspection UseTomlInstead
    //ksp("io.github.raamcosta.compose-destinations:$destinationVersion")

    //Room
    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.common)
    //val roomVersion = "2.6.1"
    //implementation(libs.androidx.room.runtime)//{rootProject.extra["room_version"]}")
    //ksp(libs.androidx.room.compiler)//${rootProject.extra["room_version"]}")
    //implementation(libs.androidx.room.ktx)//${rootProject.extra["room_version"]}")

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