pluginManagement {
    repositories {
        maven { url = uri("https://chaquo.com/maven-test") }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "bluetooth_c"
include(":app")
 