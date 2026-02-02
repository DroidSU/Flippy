pluginManagement {
    repositories {
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

rootProject.name = "Flippy"
include(":app")
include(":core")
include(":auth")
include(":game-engine")
include(":common")
include(":database")
include(":feature:profile")
include(":feature:settings")
include(":feature:leaderboard")
