import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}


group = "com.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}
tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
android {
    namespace = "ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani"
    compileSdk = 34

    defaultConfig {
        applicationId = "ca.qc.bdeb.c5gm.cinejournal.sabbaghziarani"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        val AWS_ACCESS_KEY_ID = gradleLocalProperties(rootDir)
            .getProperty("AWS_ACCESS_KEY_ID")
        val AWS_SECRET_ACCESS_KEY = gradleLocalProperties(rootDir)
            .getProperty("AWS_SECRET_ACCESS_KEY")
        val API_KEY_TMDB = gradleLocalProperties(rootDir)
            .getProperty("API_KEY_TMDB")
        debug {
            buildConfigField("String", "AWS_ACCESS_KEY_ID", AWS_ACCESS_KEY_ID)
            buildConfigField("String", "AWS_SECRET_ACCESS_KEY", AWS_SECRET_ACCESS_KEY)
            buildConfigField("String", "API_KEY_TMDB", API_KEY_TMDB)
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "AWS_ACCESS_KEY_ID", AWS_ACCESS_KEY_ID)
            buildConfigField("String", "AWS_SECRET_ACCESS_KEY", AWS_SECRET_ACCESS_KEY)
            buildConfigField("String", "API_KEY_TMDB", API_KEY_TMDB)

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore-ktx:24.8.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    val lifecycle_version = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    val room_version = "2.5.2"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    ksp("androidx.room:room-compiler:$room_version")
    // optional- Kotlin Extensions and Coroutines support for Room
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    //prototype
    implementation("com.google.android.material:material:1.10.0")
    implementation("aws.sdk.kotlin:s3:0.25.0-beta")
    implementation("aws.sdk.kotlin:dynamodb:0.25.0-beta")
    implementation("aws.sdk.kotlin:iam:0.25.0-beta")
    implementation("aws.sdk.kotlin:cloudwatch:0.25.0-beta")
    implementation("aws.sdk.kotlin:cognitoidentityprovider:0.25.0-beta")
    implementation("aws.sdk.kotlin:sns:0.25.0-beta")
    implementation("aws.sdk.kotlin:pinpoint:0.25.0-beta")
    implementation("aws.sdk.kotlin:rekognition:0.30.1-beta")


// test dependency
    testImplementation(kotlin("test"))

}