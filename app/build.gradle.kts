plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gity.kliksewa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gity.kliksewa"
        minSdk = 28
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // Logging
    implementation(libs.timber)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)

    // ViewModel, LiveData & Runtime Database Local Room
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)

    // Dagger hilt
    //noinspection UseTomlInstead
    implementation("com.google.dagger:hilt-android:2.51.1")
    //noinspection UseTomlInstead
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    // Coil untuk loading gambar
    //noinspection UseTomlInstead
    implementation("io.coil-kt:coil:2.5.0")
    //noinspection UseTomlInstead
    implementation("io.coil-kt.coil3:coil-compose:3.0.4")
    //noinspection UseTomlInstead
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.4")

    // Paging 3
    //noinspection UseTomlInstead
    implementation("androidx.paging:paging-runtime-ktx:3.3.5")

    //  Kebutuhan untuk menyimpan token yang aman
    //noinspection UseTomlInstead
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    //  Delegate ViewModel
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    //  Chip Navigation Bar
    //noinspection UseTomlInstead
    implementation ("com.github.ismaeldivita:chip-navigation-bar:1.4.0")

    // Import the Firebase BoM
    //noinspection UseTomlInstead
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    //noinspection UseTomlInstead
    implementation("com.google.firebase:firebase-analytics")

    // Image Auto Slider
    //noinspection UseTomlInstead
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    // Circle Image
    //noinspection UseTomlInstead
    implementation ("de.hdodenhof:circleimageview:3.1.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}