plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":kgui-core"))
    implementation(libs.paper.api)
    implementation(libs.junit.api)
    runtimeOnly(libs.junit.engine)
    implementation(libs.mockbukkit)
}
