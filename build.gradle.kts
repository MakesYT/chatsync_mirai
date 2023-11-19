plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("java-library")
    id("net.mamoe.mirai-console") version "2.15.0"

}

group = "top.ncserver"
version = "1.1.6"
mirai.jvmTarget = JavaVersion.VERSION_11

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
repositories {
    maven("https://maven.aliyun.com/repository/public")

    mavenCentral()
}
dependencies {
    implementation("com.alibaba:fastjson:2.0.22")
    implementation("org.smartboot.socket:aio-pro:1.6.1")
    implementation("net.coobird:thumbnailator:0.4.19")

}