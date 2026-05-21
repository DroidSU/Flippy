import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val googleWebClientId: String = localProperties.getProperty("google.web.client.id") ?: ""

android {
    namespace = "com.fliq"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.fliq"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", "\"$googleWebClientId\"")
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("signing.storeFile") ?: "keystore.jks")
            storePassword = localProperties.getProperty("signing.storePassword") ?: ""
            keyAlias = localProperties.getProperty("signing.keyAlias") ?: ""
            keyPassword = localProperties.getProperty("signing.keyPassword") ?: ""
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Test AdMob IDs
            manifestPlaceholders["admob_app_id"] = "ca-app-pub-3940256099942544~3347511713"
            buildConfigField("String", "ADMOB_REWARDED_INTERSTITIAL_ID", "\"ca-app-pub-3940256099942544/5354046379\"")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            
            // Fallback to test IDs if not provided in local.properties
            val appId = localProperties.getProperty("admob.app.id") ?: "ca-app-pub-3940256099942544~3347511713"
            val adUnitId = localProperties.getProperty("admob.adunit.rewarded_interstitial") ?: "ca-app-pub-3940256099942544/5354046379"
            
            manifestPlaceholders["admob_app_id"] = appId
            buildConfigField("String", "ADMOB_REWARDED_INTERSTITIAL_ID", "\"$adUnitId\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
             jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    android.sourceSets.named("main") {
        kotlin.directories += "additionalSourceDirectory/kotlin"
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":game-engine"))
    implementation(project(":common"))
    implementation(project(":database"))
    implementation(project(":feature:profile"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:leaderboard"))
    implementation(project(":feature:speed-run"))
    implementation(project(":feature:mirage"))
    implementation(project(":feature:minefield"))
    implementation(project(":feature:frenzy"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.hilt.navigation.compose)
    
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.crashlytics)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.ads)
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation(libs.firebase.appcheck.debug)

    implementation(libs.lottie.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.work.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
