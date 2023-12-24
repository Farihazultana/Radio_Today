plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("com.google.dagger.hilt.android") version "2.44.1"
}

android {
    namespace = "com.example.radiotoday"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.radiotoday"
        minSdk = 21
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Hilt
    implementation("com.google.dagger:hilt-android:2.44.1")
    kapt("com.google.dagger:hilt-android-compiler:2.44.1")

    //retrofit, gson
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //ViewModel , livedata
    val lifecycle_version = "2.6.2"
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")

    // dependency for slider view
    implementation ("com.github.denzcoskun:ImageSlideshow:0.1.2")

    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.fragment:fragment-ktx:1.6.2")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    // exo player
    implementation ("com.google.android.exoplayer:exoplayer:2.16.0")
    implementation ("com.github.wseemann:FFmpegMediaMetadataRetriever:1.0.14")
    implementation ("com.google.android.exoplayer:extension-mediasession:2.16.0")
    implementation ("com.google.android.exoplayer:extension-cronet:2.16.0")
    implementation ("com.google.android.exoplayer:extension-ima:2.16.0")

    //Google Sign In
    implementation ("com.google.android.gms:play-services-auth:20.7.0")

    //Facebook Sign In
    implementation ("com.facebook.android:facebook-login:latest.release")
    implementation ("com.facebook.android:facebook-login:16.3.0")

    implementation ("com.github.mahimrocky:ShowMoreText:1.0.2")
}

kapt {
    correctErrorTypes = true
}