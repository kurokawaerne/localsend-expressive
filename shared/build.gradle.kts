import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

compose.resources {
    publicResClass = true
    packageOfResClass = "dev.kurokawa.localsend.expressive.resources"
    generateResClass = org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration.Always
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    jvm("desktop") {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }

    sourceSets {
        // Intermediate source set shared by the two JVM-based targets
        // (Android + desktop JVM): java.net and Ktor CIO live here.
        val jvmMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain {
            dependsOn(jvmMain)
        }
        val desktopMain by getting {
            dependsOn(jvmMain)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            // Compose Resources runtime: the generated Res accessors (fonts)
            // and org.jetbrains.compose.resources.Font live here.
            implementation(compose.components.resources)
            implementation(libs.compose.material3)
            implementation(libs.material.symbols.outlined)
            implementation(libs.material.symbols.outlined.filled)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.json)
            // QR codes for the share-via-link address (pure Java, both platforms).
            implementation(libs.zxing.core)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.server.core)
            // Netty, not CIO: the CIO server engine cannot terminate TLS
            // ("CIO Engine does not currently support HTTPS" — startup crash).
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.content.negotiation)
            // OkHttp engine for the client: JSSE TLS can PRESENT our
            // self-signed client certificate. Official peers (v2.2) use
            // mutual TLS — the Ktor CIO client cannot present certificates
            // at all, which broke every send to the official app.
            implementation(libs.ktor.client.okhttp)
            // Self-signed TLS certificate generation (HTTPS mode, M1).
            implementation(libs.bcprov)
            implementation(libs.bcpkix)
        }
        androidMain.dependencies {
            // rememberLauncherForActivityResult for the SAF file picker (M3).
            implementation(libs.androidx.activity.compose)
            // DocumentFile tree walking for the Folder picker.
            implementation(libs.androidx.documentfile)
            // QR scanner: CameraX preview + analysis, decoded with ZXing.
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

android {
    namespace = "dev.kurokawa.localsend.expressive.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
