import org.gradle.api.GradleException
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services") apply false
    id("com.google.firebase.crashlytics") apply false
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystorePropertiesFile.exists()) {
        FileInputStream(keystorePropertiesFile).use(::load)
    }
}
val hasReleaseSigning =
    keystoreProperties.getProperty("storeFile")?.isNotBlank() == true &&
        keystoreProperties.getProperty("storePassword")?.isNotBlank() == true &&
        keystoreProperties.getProperty("keyAlias")?.isNotBlank() == true &&
        keystoreProperties.getProperty("keyPassword")?.isNotBlank() == true
val hasGoogleServicesConfig = file("google-services.json").exists()

android {
    namespace = "com.fitx.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.fitx.app"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val localConfig = Properties().apply {
            val localFile = rootProject.file("local.properties")
            if (localFile.exists()) {
                FileInputStream(localFile).use(::load)
            }
        }
        val usdaApiKey = (
            (project.findProperty("USDA_API_KEY") as String?)
                ?: localConfig.getProperty("USDA_API_KEY")
                ?: System.getenv("USDA_API_KEY")
            ).orEmpty()
        val youtubeApiKey = (
            (project.findProperty("YOUTUBE_API_KEY") as String?)
                ?: localConfig.getProperty("YOUTUBE_API_KEY")
                ?: System.getenv("YOUTUBE_API_KEY")
            ).orEmpty()
        val updateInfoUrl =
            (project.findProperty("UPDATE_INFO_URL") as String?)
                ?: "https://raw.githubusercontent.com/saixsanthosh/fitx/main/version.json"
        val updateFallbackUrl =
            (project.findProperty("UPDATE_FALLBACK_URL") as String?)
                ?: "https://github.com/saixsanthosh/fitx/releases/latest"
        buildConfigField("String", "USDA_API_KEY", "\"$usdaApiKey\"")
        buildConfigField("String", "YOUTUBE_API_KEY", "\"$youtubeApiKey\"")
        buildConfigField("String", "UPDATE_INFO_URL", "\"$updateInfoUrl\"")
        buildConfigField("String", "UPDATE_FALLBACK_URL", "\"$updateFallbackUrl\"")
    }

    signingConfigs {
        create("release") {
            if (hasReleaseSigning) {
                storeFile = file(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

if (hasGoogleServicesConfig) {
    apply(plugin = "com.google.gms.google-services")
    apply(plugin = "com.google.firebase.crashlytics")
} else {
    logger.lifecycle("google-services.json not found. Building Fitx without Firebase Google Services plugin.")
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.10.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-service:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.navigation:navigation-compose:2.8.4")
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-session:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.hilt:hilt-work:1.2.0")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")

    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation("androidx.core:core-splashscreen:1.0.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", "$projectDir/schemas")
        arg("room.incremental", "true")
    }
}

val githubReleaseVersion = "v${android.defaultConfig.versionName ?: "0.0.0"}"
val githubApkName = "fitx-$githubReleaseVersion-release.apk"
val githubReleaseDir = rootProject.layout.projectDirectory.dir("release-artifacts").asFile

tasks.register("exportReleaseApkForGithub", Copy::class) {
    dependsOn("assembleRelease")
    if (!hasReleaseSigning) {
        doFirst {
            throw GradleException(
                "Release signing is not configured. Create keystore.properties from keystore.properties.example " +
                    "and set storeFile/storePassword/keyAlias/keyPassword."
            )
        }
    }
    from(layout.buildDirectory.file("outputs/apk/release/app-release.apk"))
    into(githubReleaseDir)
    rename { githubApkName }
}

tasks.register("generateReleaseChecksum") {
    dependsOn("exportReleaseApkForGithub")
    doLast {
        val apk = File(githubReleaseDir, githubApkName)
        if (!apk.exists()) {
            throw GradleException("Release APK not found: ${apk.absolutePath}")
        }
        val digest = MessageDigest.getInstance("SHA-256")
        apk.inputStream().use { input ->
            val buffer = ByteArray(8 * 1024)
            var read = input.read(buffer)
            while (read >= 0) {
                if (read > 0) {
                    digest.update(buffer, 0, read)
                }
                read = input.read(buffer)
            }
        }
        val checksum = digest.digest().joinToString("") { "%02x".format(it) }
        File(githubReleaseDir, "SHA256.txt").writeText("$checksum  $githubApkName\n")
    }
}

tasks.register("prepareGithubRelease") {
    group = "distribution"
    description = "Builds signed release APK + SHA256 into release-artifacts/ for GitHub Releases."
    dependsOn("generateReleaseChecksum")
}
