plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "kelompok3.fnmtv.fnmtvmobile"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "kelompok3.fnmtv.fnmtvmobile"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // RETROFIT & GSON (Jembatan komunikasi ke API Laravel)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Buat ubah JSON dari Laravel jadi Data Class Kotlin

    // OKHTTP LOGGING (Satpam buat ngintip error/response API di Logcat - super penting buat debug)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // LIFECYCLE & VIEWMODEL (Otak dari MVVM - biar data aman pas layar HP dimiringin)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // COROUTINES (Biar pas nembak API ke server, aplikasinya ga nge-freeze / ngelag)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // GLIDE (Buat nampilin 'foto_thumbnail' berita dari URL server Laravel lu)
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // PhotoView buat bikin gambar bisa di-zoom (Pinch-to-zoom)
    implementation("com.github.chrisbanes:PhotoView:2.3.0")

    // Volley
    implementation("com.android.volley:volley:1.2.1")
}