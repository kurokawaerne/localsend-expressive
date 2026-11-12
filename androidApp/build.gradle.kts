plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

// Release signing comes from the environment (GitHub Actions secrets);
// local builds without the variables stay unsigned, as before.
val lsexKeystoreFile = System.getenv("LSEX_KEYSTORE_FILE")

android {
    namespace = "dev.kurokawa.localsend.expressive"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "dev.kurokawa.localsend.expressive"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 9
        versionName = "0.8.0"
    }
    signingConfigs {
        if (lsexKeystoreFile != null) {
            create("release") {
                storeFile = file(lsexKeystoreFile)
                storePassword = System.getenv("LSEX_STORE_PASSWORD")
                keyAlias = System.getenv("LSEX_KEY_ALIAS") ?: "lsex"
                keyPassword = System.getenv("LSEX_KEY_PASSWORD")
                    ?: System.getenv("LSEX_STORE_PASSWORD")
            }
        }
    }
    // Split APKs per CPU architecture + a universal one (Ernest's
    // requirement, same as UDYAT Expressive): on his metered connection
    // nobody should download three architectures to use one.
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86_64")
            isUniversalApk = true
        }
    }
    buildFeatures { compose = true }
    packaging {
        resources {
            // BouncyCastle (and jspecify) jars ship the same OSGi manifest path;
            // the APK merge cannot keep four copies of it.
            excludes += "META-INF/versions/9/OSGI-INF/MANIFEST.MF"
            // Every Netty jar ships these same paths.
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/INDEX.LIST"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            // Signed, non-debuggable releases (v0.6.0): Android's
            // debuggable-app compatibility nag (16 KB dialog) disappears.
            if (lsexKeystoreFile != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material3)
    implementation(libs.androidx.core.splashscreen)
}
