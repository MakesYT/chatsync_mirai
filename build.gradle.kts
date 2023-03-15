plugins {
    val kotlinVersion = "1.7.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.4"

}

group = "top.ncserver"
version = "1.0.4"
mirai.jvmTarget = JavaVersion.VERSION_11

repositories {
    maven("https://maven.aliyun.com/repository/public")

    mavenCentral()
}
dependencies {
    implementation("com.alibaba:fastjson:2.0.22")
    implementation("org.smartboot.socket:aio-pro:1.6.1")
    implementation("net.coobird:thumbnailator:0.4.19")

}