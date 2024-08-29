// Top-level build file where you can add configuration options common to all sub-projects/modules.
val navVersion: String by extra { "2.7.7" }
val lottieVersion: String by extra { "6.1.0" }
val intuit: String by extra { "1.0.6" }
val hiltVersion: String by extra { "2.48" }
val hiltCompilerVersion: String by extra { "1.1.0" }

plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "2.0.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("com.google.devtools.ksp") version "2.0.10-1.0.24" apply false
    id("androidx.navigation.safeargs") version "2.7.7" apply false
}