plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projetws"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.projetws"
        minSdk = 29
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.core:core:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("androidx.recyclerview:recyclerview:1.0.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.crashlytics.buildtools)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}