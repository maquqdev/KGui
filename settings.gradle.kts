plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "KGui"

include("kgui-core")
include("kgui-test")
include("kgui-example")
