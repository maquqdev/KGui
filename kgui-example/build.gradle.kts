plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":kgui-core"))
    compileOnly(libs.paper.api)
}

tasks.processResources {
    filesMatching("paper-plugin.yml") {
        expand("version" to project.version)
    }
}
