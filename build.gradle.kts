import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application

    id("com.github.johnrengelman.shadow") version "6.0.0"

    kotlin("jvm") version "1.7.0"
}

group = "Bot"
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:4.4.0_352")
    implementation("com.google.cloud:google-cloud-translate:1.88.0")
    implementation("com.github.Walkyst:lavaplayer-fork:1.3.96")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("com.github.Doomsdayrs:Jikan4java:1.4.2")
    implementation("org.json:json:20210307")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("net.lingala.zip4j:zip4j:2.8.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
        name = "m2-dv8tion"
    }
    maven {
        url = uri("https://dl.bintray.com/sedmelluq/com.sedmelluq")
        maven { url = uri("https://jitpack.io") }
    }
}

// Define the main class for the application
application {
    mainClass.set("Bot.App")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
